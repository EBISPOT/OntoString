package uk.ac.ebi.spot.ontostring.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontostring.domain.auth.User;
import uk.ac.ebi.spot.ontostring.service.JWTService;
import uk.ac.ebi.spot.ontostring.service.ProjectService;
import uk.ac.ebi.spot.ontostring.service.UserService;
import uk.ac.ebi.spot.ontostring.system.GeneralCommon;
import uk.ac.ebi.spot.ontostring.util.CurationUtil;
import uk.ac.ebi.spot.ontostring.util.HeadersUtil;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;
import uk.ac.ebi.spot.ontostring.constants.ProjectRole;
import uk.ac.ebi.spot.ontostring.rest.assembler.UserDtoAssembler;
import uk.ac.ebi.spot.ontostring.rest.dto.users.ProjectUserDto;
import uk.ac.ebi.spot.ontostring.rest.dto.users.UserDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class ProjectUsersController {

    private static final Logger log = LoggerFactory.getLogger(ProjectUsersController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * GET /v1/projects/{projectId}/users
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_USERS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getProjectUsers(@PathVariable String projectId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to retrieve users for project: {}", user.getEmail(), projectId);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN}));
        List<User> users = userService.findByProjectId(projectId);
        return users.stream().map(UserDtoAssembler::assemble).collect(Collectors.toList());
    }

    /**
     * POST /v1/projects/{projectId}/users
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_USERS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUserToProject(@RequestBody @Valid ProjectUserDto projectUserDto, @PathVariable String projectId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to add user to project [{}]", user.getEmail(), projectId, projectUserDto.getUser().getEmail());
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN}));
        User targetUser = UserDtoAssembler.disassemble(projectUserDto.getUser());

        List<ProjectRole> projectRoles = CurationUtil.rolesFromStringList(projectUserDto.getRoles());
        targetUser = userService.addUserToProject(targetUser, projectId, projectRoles);
        return UserDtoAssembler.assemble(targetUser);
    }

    /**
     * PUT /v1/projects/{projectId}/users/{userId}
     */
    @PutMapping(value = "/{projectId}" + CurationConstants.API_USERS + "/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUserToProject(@RequestBody @Valid ProjectUserDto projectUserDto, @PathVariable String projectId, @PathVariable String userId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to update user roles [{}] in project [{}]", user.getEmail(), userId, projectId);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN}));
        User targetUser = UserDtoAssembler.disassemble(projectUserDto.getUser());
        List<ProjectRole> projectRoles = CurationUtil.rolesFromStringList(projectUserDto.getRoles());
        targetUser = userService.updateUserRoles(targetUser, projectId, projectRoles);
        return UserDtoAssembler.assemble(targetUser);
    }

    /**
     * DELETE /v1/projects/{projectId}/users/{userId}
     */
    @DeleteMapping(value = "/{projectId}" + CurationConstants.API_USERS + "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUserFromProject(@PathVariable String projectId, @PathVariable String userId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to remove user roles [{}] in project [{}]", user.getEmail(), userId, projectId);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN}));
        User targetUser = userService.findById(userId);
        userService.removeProjectFromUser(targetUser, projectId);
    }
}
