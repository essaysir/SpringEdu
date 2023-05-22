package com.spring.board.common;
/*
※ 관련용어 ※
 - 암호(Cryptography) : 해독 불가능한 형태로 변환하거나 또는 암호화된 메시지를 해독 가능한 형태로 변환하는 기술
 - 평문(Plaintext)    : 해독 가능한 형태의 메시지
 - 암호문(Ciphertext) : 해독 불가능한 형태의 메시지
 - 암호화(Encryption) : 평문을 암호문으로 변환하는 과정
 - 복호화(Decryption) : 암호문을 평문으로 변환하는 과정
 - 대칭키 암호(또는 비밀키 암호) : 암호화키와 복호화키가 같은 암호
 - 비대칭키 암호(또는 공개키 암호) : 암호화키와 복호화키가 다른 암호


※ 단방향(일방향) 암호화 알고리즘(SHA-256 Secure Hash Algorithm) ※

  단방향 암호화는 평문을 암호화했을때 다시 평문으로 되돌리는 것(복호화)을 할 수 없는 암호화 이다. 
  많이 사용하고 있는 알고리즘은 SHA-256 암호화이다. 
  이 알고리즘은 주로 사용자의 패스워드 에 사용하는데, 패스워드의 경우 복호화해서 식별할 필요가 없기 때문이다. 
 1과 같은 단순한 값을 SHA-256으로 암호화했을 경우 항상 같은 값이 나오기 때문에 
  비밀번호 입력을 적당한 길이와 복잡성을 가지도록 유도하여 패스워드를 유추하는데 어렵도록 해야 한다.
  해시 함수가 출력하는 압축된 문장을 다이제스트(Digest)라고 하는데, 다이제스트의 출력길이는
 224, 256, 384, 512 bit 로 다양하다. 이때 256bit의 출력 길이를 갖는 것을 SHA-256 이라고 부른다.
 */ 


import java.security.MessageDigest;

public class Sha256{
    public static String encrypt(String plainText) {
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(plainText.getBytes());
            byte byteData[] = md.digest();
           /*
        1. SHA-256으로 해시

             - MessageDigest객체 생성시 알고리즘을 "SHA-256"으로 해서 만든다. 
               해시된 데이터는 바이트 배열타입의 바이너리 데이터(이진 데이터)이다.
           */

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            /*
                해시된 데이터는 바이트 배열타입의 바이너리 데이터(이진 데이터)이므로
                이것을 16진수 문자열(String)타입으로 변환해준다.
            */


            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<byteData.length;i++) {
                String hex=Integer.toHexString(0xff & byteData[i]);
                if(hex.length()==1){
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}