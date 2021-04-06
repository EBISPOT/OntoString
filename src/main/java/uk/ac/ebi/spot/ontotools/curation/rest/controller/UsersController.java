package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.exception.AuthorizationException;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.UserDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.RestResponsePage;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.UserCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.UserDto;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_USERS)
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    /**
     * GET /v1/users?search=<search>
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RestResponsePage<UserDto> getUsers(@RequestParam(value = CurationConstants.PARAM_SEARCH, required = false) String prefix,
                                              @PageableDefault(size = 20, page = 0) Pageable pageable,
                                              HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to retrieve users: {}", user.getEmail(), prefix);
        if (!user.isSuperUser() && !CurationUtil.isAdmin(user)) {
            log.error("Attempt to retrieve users by an unauthorized user: {}", user.getEmail());
            throw new AuthorizationException("Attempt to retrieve users by an unauthorized user: " + user.getEmail());
        }
        Page<User> users = userService.retrieveUsers(prefix, pageable);
        return new RestResponsePage<>(users.stream().map(UserDtoAssembler::assemble).collect(Collectors.toList()), pageable, users.getTotalElements());
    }

    /**
     * POST /v1/users
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserCreationDto userCreationDto, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to create user: {} | {}", user.getEmail(), userCreationDto.getName(), userCreationDto.getEmail());
        if (user.isSuperUser() || CurationUtil.isAdmin(user)) {
            User created = userService.createUser(userCreationDto.getName(), userCreationDto.getEmail());
            return UserDtoAssembler.assemble(created);
        }

        log.error("Attempt to create a user by an unauthorized user: {}", user.getEmail());
        throw new AuthorizationException("Attempt to create a user by an unauthorized user: " + user.getEmail());
    }
}
