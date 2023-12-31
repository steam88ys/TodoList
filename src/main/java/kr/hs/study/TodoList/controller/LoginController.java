package kr.hs.study.TodoList.controller;

import kr.hs.study.TodoList.userEntity.Todo;
import kr.hs.study.TodoList.userEntity.User;
import kr.hs.study.TodoList.userEntity.UserService;
import kr.hs.study.TodoList.dto.TodoDTO;
import kr.hs.study.TodoList.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/login_form")
    public String processLogin(@RequestParam String uname, @RequestParam String upass, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = userService.login(uname, upass);
        if (user != null) {
            session.setAttribute("user", user);

            List<TodoDTO> todolist = service.listAll(user.getEmail());
            session.setAttribute("todolist", todolist);

            return "redirect:/todolist"; // 로그인 성공 시 이동할 페이지
        } else {
            // 로그인 실패 시에 메시지를 플래시 속성으로 설정하여 리다이렉트 후 alert 메시지를 표시합니다.
            redirectAttributes.addFlashAttribute("loginError", "Invalid username or password");
            return "redirect:/login"; // 로그인 실패 시 이동할 페이지
        }
    }

    @GetMapping("/todolist")
    public String todolist_form() {
        return "todolist_form";
    }

    @Autowired
    private TodoService todoService;

    @GetMapping("/todolist_form")
    public String listAll(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser != null) {
            List<TodoDTO> joinlist = todoService.join(loggedInUser.getEmail());
            model.addAttribute("joinlist", joinlist);

            List<TodoDTO> list = todoService.listAll(loggedInUser.getEmail());
            model.addAttribute("list", list);

            List<TodoDTO> cubelist = todoService.getTodoListCube();

            // 소계, 중계, 총합계 계산
            int grandTotal = cubelist.stream().mapToInt(TodoDTO::getTotal_tasks).sum();
            int intermediate = 0;
            for (TodoDTO todoDTO : cubelist) {
                intermediate += todoDTO.getTotal_tasks();
                todoDTO.setIntermediate(intermediate);
                todoDTO.setGrand_total(grandTotal);
                todoDTO.setSubtotal(grandTotal - intermediate);
            }

            model.addAttribute("cubelist", cubelist);
            model.addAttribute("grandTotal", grandTotal);

            return "todolist";
        } else {
            return "redirect:/login?error";
        }
    }

    @PostMapping("/todolist_form")
    public String list(TodoDTO dto, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user"); // 로그인된 사용자 정보 가져오기

        if (loggedInUser != null) {
            dto.setUser_email(loggedInUser.getEmail()); // 로그인된 사용자의 이메일 설정
            service.insert(dto);

            List<TodoDTO> list = service.listAll(loggedInUser.getEmail()); // 로그인된 사용자의 할 일 목록 가져오기
            model.addAttribute("list", list);

            List<TodoDTO> cubelist = service.calculateCubelist();
            int grandTotal = 0;
            for (TodoDTO todoDTO : cubelist) {
                grandTotal += todoDTO.getTotal_tasks();
                todoDTO.setGrand_total(grandTotal);
            }
            model.addAttribute("cubelist", cubelist);

            return "todolist";
        } else {
            // 로그인되지 않은 경우 처리 (예: 에러 페이지로 리다이렉트)
            return "redirect:/login?error";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        service.delete(id);
        return "redirect:/todolist_form";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        TodoDTO dto = service.readOne(id);
        model.addAttribute("dto", dto);
        return "update_form";
    }

    @PostMapping("/update_done")
    public String update_done(TodoDTO dto) {
        service.update(dto);
        return "redirect:/todolist_form";
    }

    @GetMapping("/todos/{todo_date}")
    public String todos(@PathVariable("todo_date") String todoDate, TodoDTO dto, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        List<TodoDTO> todolist = service.readDate(dto, loggedInUser.getEmail(),todoDate);
        model.addAttribute("todoList", todolist);
        return "todo_list_template";
    }


}
