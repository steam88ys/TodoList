package kr.hs.study.TodoList.controller;

import kr.hs.study.TodoList.userEntity.User;
import kr.hs.study.TodoList.userEntity.UserService;
import kr.hs.study.TodoList.dto.TodoDTO;
import kr.hs.study.TodoList.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private TodoService service;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login_form"; // 로그인 폼 템플릿 (login_form.html 등)
    }

    @PostMapping("/login_form")
    public String processLogin(@RequestParam String uname, @RequestParam String upass, HttpSession session) {
        User user = userService.login(uname, upass);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/todolist"; // 로그인 성공 시 이동할 페이지
        } else {
            return "redirect:/login?error"; // 로그인 실패 시 이동할 페이지
        }
    }

//    @GetMapping("/login")
//    public String login() {
//        return "login_form";
//    }
//
    @GetMapping("/todolist")
    public String todolist_form() {
        return "todolist_form";
    }

    @GetMapping("/todolist_form")
    public String listAll(TodoDTO dto, Model model) {
        List<TodoDTO> list = service.listAll();
        model.addAttribute("list", list);
        List<TodoDTO> cubelist = service.getTodoListCube();
        model.addAttribute("cubelist", cubelist);
        return "todolist";
    }

    @PostMapping("/todolist_form")
    public String list(TodoDTO dto, Model model) {
        service.insert(dto);
        System.out.println(dto);
        List<TodoDTO> list = service.listAll();
        model.addAttribute("list", list);

        List<TodoDTO> cubelist = service.getTodoListCube();
        model.addAttribute("cubelist", cubelist);
        return "todolist";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        service.delete(id);
        return "redirect:/todolist_form";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        // select로 내용 가져오기
        TodoDTO dto = service.readOne(id);
        model.addAttribute("dto", dto);
        return "update_form";
    }

    @PostMapping("/update_done")
    public String update_done(TodoDTO dto) {
        System.out.println(dto);
        service.update(dto);
        System.out.println(dto);
        return "redirect:/todolist_form";
    }

}
