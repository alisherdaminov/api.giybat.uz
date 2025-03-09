package api.giybat.uz.util;

import api.giybat.uz.dto.JwtDTO;
import api.giybat.uz.entity.ProfileRoleEntity;
import api.giybat.uz.enums.ProfileRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

public class JwtUtil {
    private static final int tokenLiveTime = 1000 * 3600 * 24; // 1-day
    private static final String secretKey = "veryLongSecretmazgillattayevlasharaaxmojonjinnijonsurbetbekkiydirhonuxlatdibekloxovdangasabekochkozjonduxovmashaynikmaydagapchishularnioqiganbolsangizgapyoqaniqsizmazgi";

    public static String encode(Long id, List<ProfileRoleEnum> roleEnumList) {
        // List<ProfileRoleEnum> rolelar kop bolgani un bitta stringga o'zgartiriladi vergul orqali roles ajratilib qabul qilinadi
        String strEnumList = roleEnumList.stream().map(Enum::name).collect(Collectors.joining(","));
        Map<String, String> extraClaims = new HashMap<>();
        extraClaims.put("roles", strEnumList);

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(String.valueOf(id))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLiveTime))
                .signWith(getSignInKey())
                .compact();
    }

    public static JwtDTO decode(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Integer id = Integer.valueOf(claims.getSubject());
        String strRole = (String) claims.get("roles");
        List<ProfileRoleEnum> roleEnumList = Arrays.stream(strRole.split(",")).map(ProfileRoleEnum::valueOf).toList();
        return new JwtDTO(id, roleEnumList);
    }


    public static String encode(Long id) {
//        Map<String, Object> extraClaims = new HashMap<>();
//        extraClaims.put("username", username);
//        extraClaims.put("role", role);

        return Jwts
                .builder()
                // .claims(extraClaims)
                .subject(String.valueOf(id))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                .signWith(getSignInKey())
                .compact();
    }

//    public static JwtDTO decode(String token) {
//        Claims claims = Jwts
//                .parser()
//                .verifyWith(getSignInKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//        String username = (String) claims.get("username");
//        String role = (String) claims.get("role");
//        return new JwtDTO(username, role);
//    }

    private static SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
