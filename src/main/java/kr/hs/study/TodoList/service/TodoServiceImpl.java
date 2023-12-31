package kr.hs.study.TodoList.service;

import kr.hs.study.TodoList.dao.TodoDAO;
import kr.hs.study.TodoList.dto.TodoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {
    @Autowired
    private TodoDAO dao;

    @Override
    public void insert(TodoDTO dto) {
        dao.insert(dto);
    }

    @Override
    public void delete(String id) {
        dao.delete(id);
    }

    @Override
    public TodoDTO readOne(String id) {
        return dao.readOne(id);
    }

    @Override
    public List<TodoDTO> readDate(TodoDTO dto, String loggedInUserEmail, String todoDate) {
        return dao.readDate(dto, loggedInUserEmail, todoDate);
    }

    @Override
    public void update(TodoDTO dto) {
        dao.update(dto);
    }

    @Override
    public List<TodoDTO> listAll() {
        return dao.listAll();
    }

    @Override
    public List<TodoDTO> listAll(String loggedInUserEmail) {
        return dao.listAll(loggedInUserEmail);
    }

    public List<TodoDTO> getTodoListByLatest() {
        // 최신순으로 데이터 조회
        return dao.getTodoListByLatest();
    }

    public List<TodoDTO> getTodoListByOldest() {
        // 오래된순으로 데이터 조회
        return dao.getTodoListByOldest();
    }

    public List<TodoDTO> getTodoListByPriority() {
        // 중요도순으로 데이터 조회
        return dao.getTodoListByPriority();
    }

    public List<TodoDTO> calculateCubelist() {
        return dao.calculateCubelist();
    }

    @Override
    public List<TodoDTO> getTodoListCube() {
        return dao.getTodoListCube();
    }

    @Override
    public List<TodoDTO> join(String loggedInUserEmail) {
        return dao.join(loggedInUserEmail);
    }

}
