package ru.practicum.explorewithme.ewm.server.adminapi.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewm.server.dao.CommentRepository;
import ru.practicum.explorewithme.ewm.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.ewm.server.models.Comment;
import ru.practicum.explorewithme.ewm.server.services.AdminCommentService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;

    @Override
    public void deleteCommentByAdmin(Long comId) {
        log.info("Запрос от администратора на удаление комментария {}", comId);
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> {
                    log.error("Комментарий с id {} отсутствует", comId);
                    return new NotFoundException(String.format("Комментария с идентификатором = '%s' не найдено", comId));
                });
        commentRepository.delete(comment);
        log.info("Комментарий успешно удален администратором");
    }
}
