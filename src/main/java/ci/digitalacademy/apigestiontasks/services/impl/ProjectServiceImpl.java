package ci.digitalacademy.apigestiontasks.services.impl;

import ci.digitalacademy.apigestiontasks.models.Project;
import ci.digitalacademy.apigestiontasks.repositories.ProjectRepository;
import ci.digitalacademy.apigestiontasks.services.ProjectService;
import ci.digitalacademy.apigestiontasks.services.dto.ProjectDTO;
import ci.digitalacademy.apigestiontasks.services.mapper.ProjectMapper;
import ci.digitalacademy.apigestiontasks.services.mapping.ProjectMapping;
import ci.digitalacademy.apigestiontasks.utils.SlugifyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;

    @Override
    public ProjectDTO save(ProjectDTO projectDTO) {
        log.debug("save to project {}", projectDTO);
        Project project = projectMapper.toEntity(projectDTO);
        Project saveProject = projectRepository.save(project);
        return projectMapper.fromEntity(saveProject);
    }

    @Override
    public ProjectDTO saveProject(ProjectDTO projectDTO) {
        log.debug("Request to save project and slug {}", projectDTO);
        final String slug = SlugifyUtils.generate(projectDTO.getNameProject());
        projectDTO.setSlug(slug);
        return save(projectDTO);
    }

    @Override
    public ProjectDTO update(ProjectDTO projectDTO) {
        return null;
    }

    @Override
    public Optional<ProjectDTO> findOne(Long id) {
        return projectRepository.findById(id).map(project -> {
            return projectMapper.fromEntity(project);
        });
    }

    @Override
    public List<ProjectDTO> findAll() {
        return projectRepository.findAll().stream().map(project -> {
            return projectMapper.fromEntity(project);
        }).toList();
    }

    @Override
    public void delete(Long id) {
        projectRepository.deleteById(id);

    }

    @Override
    public ProjectDTO update(ProjectDTO projectDTO, Long id) {
        return findOne(projectDTO.getId()).map(existingProject -> {
            existingProject.setNameProject(projectDTO.getNameProject());
            existingProject.setDescription(projectDTO.getDescription());
            existingProject.setStatus(projectDTO.getStatus());
            existingProject.setStartDate(projectDTO.getStartDate());
            existingProject.setEndDate(projectDTO.getEndDate());
            return save(existingProject);
        }).orElseThrow(()->new IllegalArgumentException());
    }

    @Override
    public ProjectDTO partialupdate(ProjectDTO projectDTO, Long id) {
        return projectRepository.findById(id).map(project -> {
            ProjectMapping.partialUpdate(project, projectDTO);
            return project;
        }).map(projectRepository::save).map(projectMapper::fromEntity).orElse(null);
    }

    @Override
    public Optional<ProjectDTO> findOneBySlug(String slug) {
        log.debug("Request to get teacher by slug");
        return projectRepository.findBySlug(slug).map(teacher -> {
            return projectMapper.fromEntity(teacher);
        });
    }
}