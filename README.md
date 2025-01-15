# Recipe Web Application
사용자가 냉장고에 있는 재료를 효율적으로 활용하여 레시피를 관리하고 공유할 수 있도록 도와주는 웹 애플리케이션입니다.

## Project Overview
이 웹 애플리케이션은 다음과 같은 기능을 제공합니다:
- 레시피 관리
- 레시피 공유
- 냉장고 재료를 기반으로 한 레시피 추천

이 플랫폼은 사용자가 냉장고에 남아 있는 재료를 효율적으로 활용하여 **음식물 낭비를 줄이는 것**을 목표로 합니다.

## Features
- **User Registration & Login**
  - 회원가입 및 로그인 기능을 제공합니다.
- **Recipe Management**
  - 레시피를 추가, 수정, 삭제, 조회할 수 있습니다.
- **Recipe Sharing**
  - 다른 사용자와 레시피를 공유할 수 있는 페이지를 제공합니다.
- **Personal Refrigerator Management**
  - 냉장고 재료와 유통기한을 저장할 수 있습니다.
  - 저장된 재료를 기반으로 추천 레시피를 확인할 수 있습니다.
- **Admin Dashboard**
  - 인기 게시글과 사용자 활동 데이터를 시각화된 그래프로 제공합니다.

## Tech Stack
- **Backend**: Spring Boot, Spring Data JPA, QueryDSL, Spring Security
- **Frontend**: Thymeleaf, Bootstrap
- **Database**: MariaDB
- **Build Tool**: Gradle
- **Other Tools**: Lombok, ModelMapper, SpringDoc OpenAPI
