package ru.practicum.explorewithme.ewm.server.adminapi.compilations.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationRequest;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.ewm.server.models.Compilation;
import ru.practicum.explorewithme.ewm.server.models.Event;
import ru.practicum.explorewithme.ewm.server.services.CompilationService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.explorewithme.ewm.server.TestData.createCompilationRequest;
import static ru.practicum.explorewithme.ewm.server.TestData.createUpdateCompilationRequest;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CompilationServiceIntegrationTest {

    private final EntityManager em;
    private final CompilationService compilationService;

    @Test
    public void checkSuccessCreateCompilation() {
        NewCompilationRequest newCompilationRequest = createCompilationRequest();
        CompilationDto compilationDto = compilationService.createCompilation(newCompilationRequest);

        TypedQuery<Compilation> query = em.createQuery("Select c from Compilation c where c.id = :id", Compilation.class);
        Compilation compilation = query.setParameter("id", compilationDto.getId()).getSingleResult();

        assertThat(compilation.getId(), equalTo(compilationDto.getId()));
        assertThat(compilation.getEvents(), notNullValue());
        assertThat(compilation.getEvents().size(), equalTo(1));
        assertThat(compilation.getPinned(), equalTo(newCompilationRequest.getPinned()));
        assertThat(compilation.getTitle(), equalTo(newCompilationRequest.getTitle()));

        Event event = compilation.getEvents().stream().findFirst().orElse(new Event());
        assertThat(event.getId(), equalTo(newCompilationRequest.getEvents().getFirst()));
    }

    @Test
    public void checkSuccessUpdateCompilation() {
        UpdateCompilationRequest updateCompilationRequest = createUpdateCompilationRequest();
        CompilationDto compilationDto = compilationService.updateCompilation(1L, updateCompilationRequest);

        TypedQuery<Compilation> query = em.createQuery("Select c from Compilation c where c.id = :id", Compilation.class);
        Compilation compilation = query.setParameter("id", compilationDto.getId()).getSingleResult();

        assertThat(compilation.getId(), equalTo(compilationDto.getId()));
        assertThat(compilation.getEvents(), notNullValue());
        assertThat(compilation.getEvents().size(), equalTo(1));
        assertThat(compilation.getPinned(), equalTo(updateCompilationRequest.getPinned()));
        assertThat(compilation.getTitle(), equalTo(updateCompilationRequest.getTitle()));

        Event event = compilation.getEvents().stream().findFirst().orElse(new Event());
        assertThat(event.getId(), equalTo(updateCompilationRequest.getEvents().getFirst()));
    }

    @Test
    public void checkDeleteCompilation() {
        NewCompilationRequest newCompilationRequest = createCompilationRequest();
        CompilationDto compilationDto = compilationService.createCompilation(newCompilationRequest);
        assertThat(compilationDto, notNullValue());

        compilationService.deleteCompilation(compilationDto.getId());

        TypedQuery<Compilation> query = em.createQuery("Select c from Compilation c where c.id = :id", Compilation.class);

        int count = query.setParameter("id", compilationDto.getId()).getResultList().size();
        assertThat(count, equalTo(0));

        TypedQuery<Event> eventTypedQuery = em.createQuery("Select e from Event e where e.id = :id", Event.class);
        int eventCount = eventTypedQuery.setParameter("id", newCompilationRequest.getEvents().getFirst()).getResultList().size();
        assertThat(eventCount, equalTo(1));
    }
}
