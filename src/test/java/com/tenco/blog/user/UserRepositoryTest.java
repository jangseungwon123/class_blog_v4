package com.tenco.blog.user;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(UserRepository.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired //처리 DI
    private UserRepository userRepository;


    @Test
    public void findByusernameAndPassword_로그인_성공_테스트() {
        String username = "ssar";
        String password = "1234";

        User user = userRepository.findByUsernameAndPassword(username,password);
        Assertions.assertThat(user).isNotNull(); // 로그인 성공
        Assertions.assertThat(user.getUsername()).isEqualTo("ssar");
    }

    @Test
    public void save_회원가입_테스트() {
        //given : 회원 가입시 사용할 사용자 정보
        User user = User.builder()
                .username("testUser")
                .email("a@naver.com")
                .password("asd1234")
                .build();
        //when

       User savedUser = userRepository.save(user);

        //then

        //id 할당 여부 확인
        Assertions.assertThat(savedUser.getId()).isNotNull();
        // 데이터가 정상 등록 확인
        Assertions.assertThat(savedUser.getUsername()).isEqualTo("testUser");

        //원본 유저 object와 영속화 된 object가 동일한 객체인지(참조) 확인
        // 영속성 컨텍스트는 같은 엔티티에 대해 같은 인스턴스를 보장
        Assertions.assertThat(user).isSameAs(savedUser);
    }

    @Test
    public void findByUsername_사용자_조회_테스트(){
        // given

        // when
//        User nameFind = userRepository.findByUsername();
        // then
    }
    @Test
    public void findByUsername_존재하지_않는_사용자(){
        // given
        String username = "nonUser";
        // when
        User user = userRepository.findByUsername(username);
        // then
        Assertions.assertThat(user).isNull();
    }

}
