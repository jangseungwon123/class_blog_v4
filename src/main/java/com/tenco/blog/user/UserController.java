package com.tenco.blog.user;

import com.tenco.blog._core.errors.exception.Exception400;
import com.tenco.blog._core.errors.exception.Exception401;
import com.tenco.blog.board.BoardController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(BoardController.class);
    private final UserRepository userRepository;
    // httpSession <-- 세션 메모리지에 접근을 할 수 있다.
    private final HttpSession httpSession;

    // 주소 설계 : http://localhost:8080/user/update-form
    @GetMapping("/user/update-form")
    public String updateForm(HttpServletRequest request, HttpSession session) {

        log.info("회원 정보 수정 폼 요청");

        User sessionUser = (User) session.getAttribute("sessionUser");

        request.setAttribute("user", sessionUser);
        return "user/update-form";
    }


    @PostMapping("/user/update")
    public String update(UserRequest.UpdateDTO reqDTO,
                         HttpSession session, HttpServletRequest request) {
        log.info("회원 정보 수정 요청");
        User sessionUser = (User) session.getAttribute("sessionUser");
        reqDTO.validate();
        User updateUser = userRepository.updateById(sessionUser.getId(), reqDTO);
        session.setAttribute("sessionUser", updateUser);
        return "redirect:/user/update-form"; // 아스키코드만 그리고 공백도 안됨
    }


    @GetMapping("/join-form")
    public String join_form() {
        log.info("회원 가입 요청 폼");
        return "user/join-form";
    }

    // 회원 가입 액션 처리
    @PostMapping("/join")
    public String join(UserRequest.JoinDTO joinDTO, HttpServletRequest request) {
        log.info("회원 가입 요청 폼");
        log.info("사용자 명 : {}", joinDTO.getUsername());
        log.info("사용자 이메일 : {}", joinDTO.getEmail());
        joinDTO.validate();
        User existUser = userRepository.findByUsername(joinDTO.getUsername());
        if (existUser != null) {
            throw new Exception401("이미존재하는 사용자 명입니다."
                    + joinDTO.getUsername());
        }
        User user = joinDTO.toEntity();
        userRepository.save(user);
        return "redirect:/login-form";
    }


    @GetMapping("/login-form")
    public String loginForm() {
        log.info("로그인 요청 폼");
        return "user/login-form";
    }


    @PostMapping("/login")
    public String login(UserRequest.LoginDTO loginDTO) {
        log.info("===로그인 시도===");
        log.info("사용자 명 : {}", loginDTO.getUsername());
        loginDTO.validate();
        User user = userRepository.findByUsernameAndPassword(loginDTO.getUsername(),
                loginDTO.getPassword());
        if (user == null) {
            throw new Exception400("사용자명 또는 비밀번호가 틀렸어");
        }
        httpSession.setAttribute("sessionUser", user);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout() {
        log.info("로그아웃");
        httpSession.invalidate();
        return "redirect:/";
    }


}
