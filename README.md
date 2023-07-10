# Clova
  
### 📝 About project <img align="right" width="200" alt="chat" src="https://github.com/wjdgml3092/Clova/assets/59546994/98fabdf7-b648-4617-ac98-acbe9e0fa0ae">
  
**2021 정보시스템 캡스톤디자인 전공과목 팀프로젝트입니다.**

"당신의 하루를 정리하세요, 클로바"

**인원** : 2명 
<br/>
**앱 프론트엔드 및 DB 구축을 담당**하였습니다.

**기술스택**
<p>
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/android-F6F6F6?style=for-the-badge&logo=android&logoColor=white%22"/>
  <img src="https://img.shields.io/badge/firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=white">
</p>

### 📌 서비스 설명

> 클로바는 자신의 마음을 표현하는 감성 일기 등으로 스트레스를 해소하려는 사람들이 늘어나는 추세에 맞춰,<br/>
**하루를 정리할 수 있는 나만의 공간을 제공**합니다.<br/><br/>
음성으로도 쉽게 일기를 작성하기 위해 안드로이드에서 제공하는 RecognizerIntent 클래스를 사용하여 **음성인식을 구현**하였고, <br/>
직접 그림을 그려 **그림일기**의 얫추억도 떠오를수 있도록 기능을 추가하였습니다.  <br/>
일기의 감정, 해시태그, 횟수를 카운팅하여 **통계**를 메인페이지에서 확인 가능합니다. 
<br/>

### 🔐 기능구현 ([김정희](https://github.com/wjdgml3092))
1. 회원관리
   - 파이어베이스의 저장된 데이터와 일치하는지 확인
   - 인증하지 않은 회원이라면, 인증 이메일 발송
   - 인증한 회원이라면, 메인 페이지로 이동
2. 통계 전반적인 기능 구현
   - 작성글 개수 카운팅
   - 감정 카운팅
   - 해시태그 통계
   - 달성률
3. 일기 관련 기능 구현
   - 목록
   - 작성 / 수정
     - 이미지 등록 -> 파이어베이스 스토리지 저장
     - 날짜, 제목, 감정, 해시태그, 본문 가능
4. 일기 작성 시 음성인식 구현
    -> RecognizerIntent 클래스 사용

<br/>

### 🖼️ 화면
<img width="400" src="https://github.com/wjdgml3092/Clova/assets/59546994/06e9eef8-8d6d-455b-9b44-67083cf155fc"/>

<br/>

### 📇 Firebase
<img width="800" src="https://github.com/wjdgml3092/Clova/assets/59546994/5cba3eca-752e-4e38-b3fd-7c87ac9b4b3d"/>
<img width="800" src="https://github.com/wjdgml3092/Clova/assets/59546994/664f54eb-ae5f-4c96-aa46-39b4ae3939ab"/>
