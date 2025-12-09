package com.kh.final3.service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.final3.configuration.JwtProperties;
import com.kh.final3.dao.TokenDao;
import com.kh.final3.dto.MemberDto;
import com.kh.final3.dto.TokenDto;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.vo.TokenVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;



@Service
public class TokenService {
	@Autowired
	private JwtProperties jwtProperties;
	@Autowired
	private TokenDao tokenDao;
	
	public String generateAccessToken(MemberDto memberDto) {
		String keyStr = jwtProperties.getKeyStr();//설정파일에 있는 keyStr값
		SecretKey key = Keys.hmacShaKeyFor(keyStr.getBytes(StandardCharsets.UTF_8));
		//만료시간 설정
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();//현재 시각
		c.add(Calendar.MINUTE, jwtProperties.getExpiration());
		//c.add(Calendar.SECOND, 5);//테스트용 코드
		Date expire = c.getTime();//만료 시각
		
		//JWT 토큰 생성
		return Jwts.builder()
				.signWith(key)//토큰 해독에 사용할 키 설정
				.expiration(expire)//토큰의 만료 시각 설정
				.issuedAt(now)//발행 시각 설정
				.issuer(jwtProperties.getIssuer())//발행자 (위변조 방지용)
				.claim("loginId", memberDto.getMemberId())//정보 추가(key,value)
				.claim("loginLevel", memberDto.getMemberRole())//정보 추가(key,value)
			.compact();
	}
	public String generateAccessToken(TokenVO tokenVO) {
		return generateAccessToken(MemberDto.builder()
					.memberId(tokenVO.getLoginId())
					.memberRole(tokenVO.getLoginLevel())
				.build());
	}
	
	
	public String generateRefreshToken(MemberDto memberDto) {
		String keyStr = jwtProperties.getKeyStr();//설정파일에 있는 keyStr값
		SecretKey key = Keys.hmacShaKeyFor(keyStr.getBytes(StandardCharsets.UTF_8));

		//만료시간 설정
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();//현재 시각
		//c.add(Calendar.MINUTE, 4 * 7 * 24 * 60);//4주
		//c.add(Calendar.DATE, 28);//4주
		c.add(Calendar.DATE, jwtProperties.getRefreshExpiration());
		Date expire = c.getTime();//만료 시각
		
		//JWT 토큰 생성
		String token = Jwts.builder()
				.signWith(key)//토큰 해독에 사용할 키 설정
				.expiration(expire)//토큰의 만료 시각 설정
				.issuedAt(now)//발행 시각 설정
				.issuer(jwtProperties.getIssuer())//발행자 (위변조 방지용)
				.claim("loginId", memberDto.getMemberId())//정보 추가(key,value)
				.claim("loginLevel", memberDto.getMemberRole())//정보 추가(key,value)
			.compact();
		
		//같은 아이디로 저장된 발행 내역을 모두 삭제
		tokenDao.deleteByTarget(memberDto.getMemberId());
		
		//DB 저장 (액세스 토큰과 달라지는 작업)
		tokenDao.insert(TokenDto.builder()
					.targetId(memberDto.getMemberId())//누구에게
					.value(token)//무슨토큰을 발행했는지
				.build());
		
		//토큰 반환
		return token;
	}
	public String generateRefreshToken(TokenVO tokenVO) {
		return generateRefreshToken(MemberDto.builder()
				.memberId(tokenVO.getLoginId())
				.memberRole(tokenVO.getLoginLevel())
			.build());
	}
	public TokenVO parse(String authorization) {
		if(authorization.startsWith("Bearer ") == false)//Bearer 토큰이 아니라면
			throw new UnauthorizationException();//예외 처리!
		
		//앞 7글자 제거 (B.e.a.r.e.r. )
		//String token = authorization.substring("Bearer ".length());
		String token = authorization.substring(7);
		
		SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getKeyStr().getBytes(StandardCharsets.UTF_8));
		Claims claims = (Claims) Jwts.parser()
				.verifyWith(key)
				.requireIssuer(jwtProperties.getIssuer())
			.build()
				.parse(token)
				.getPayload();
		//claims에 담긴 데이터를 TokenVO에 옮겨담아서 반환
		return TokenVO.builder()
					.loginId((String)claims.get("loginId"))
					.loginLevel((String)claims.get("loginLevel"))
				.build();
	}
	//JWT 토큰의 만료까지 남은시간을 구하는 기능
		public long getRemain(String bearerToken) {
			if(bearerToken.startsWith("Bearer ") == false)//Bearer 토큰이 아니라면
				throw new UnauthorizationException();//예외 처리!
			
			//앞 7글자 제거 (B.e.a.r.e.r. )
			String token = bearerToken.substring(7);
			
			SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getKeyStr().getBytes(StandardCharsets.UTF_8));
			Claims claims = (Claims) Jwts.parser()
					.verifyWith(key)
					.requireIssuer(jwtProperties.getIssuer())
				.build()
					.parse(token)
					.getPayload();
			
			Date expire = claims.getExpiration();//만료시각 추출
			//return expire.getTime() - System.currentTimeMillis();//만료시각 - 현재시각
			Date now = new Date();
			return expire.getTime() - now.getTime();//만료시각 - 현재시각 (무조건 0이상)
		}
		public boolean checkRefreshToken(TokenVO tokenVO, String refreshToken) {
			//조회
			TokenDto tokenDto = tokenDao.selectOne(
				TokenDto.builder()
					.targetId(tokenVO.getLoginId())//대상
					.value(refreshToken.substring(7))//토큰
				.build()
			);
			//결과 없음 = 인증 불가
			if(tokenDto == null) return false;
			
			//delete account_token where account_token_no = #{accountTokenNo}
			//accountTokenDao.delete(accountTokenDto);
			//accountTokenDao.delete(accountTokenDto.getAccountTokenNo());//하나만
			//accountTokenDao.deleteByTarget(tokenVO.getLoginId());//특정 사용자의 모든 내역
			
			//결과 있음
			return true;
		}
	
}
