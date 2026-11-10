package com.smartpraia.smartpraia.shared.exception;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.smartpraia.smartpraia.shared.exception.EmailJaCadastradoException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> tratarNaoEncontrado(
        RecursoNaoEncontradoException ex,
        HttpServletRequest request
    ){
        ApiError erro = new ApiError(
            LocalDateTime.now(), 
            404,
            "Not Found",
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarDadosInvalidos(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ){
        List<String> detalhes = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + " : " +  error.getDefaultMessage())
        .toList();

        ApiError erro = new ApiError(LocalDateTime.now(), 
        400, 
        "Bad Request",
        "Existem dados inválidos na requisição",
        request.getRequestURI(),
        detalhes);

        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> tratarConflito(
        DataIntegrityViolationException ex,
        HttpServletRequest request
    ){
        ApiError erro = new ApiError(
            LocalDateTime.now(),
            409,
            "Conflict",
            "A operação viola uma restrição do banco de dados",
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> tratarErroInterno(
        Exception ex,
        HttpServletRequest request
    ){
        ApiError erro = new ApiError(
            LocalDateTime.now(), 
            500, 
            "Internal Server Error", 
            "Ocorreu um erro interno. Tente novamente mais tarde",
            request.getRequestURI(),
            List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @ExceptionHandler(CnpjJaCadastradoException.class)
    public ResponseEntity<ApiError> tratarCnpjDuplicado(
        CnpjJaCadastradoException ex,
        HttpServletRequest request
    ){
        ApiError erro = new ApiError(
            LocalDateTime.now(),
            409,
            "Conflict",
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ApiError> tratarEmailJaCadastrado(
        EmailJaCadastradoException ex,
        HttpServletRequest request
    ){
        ApiError erro = new ApiError(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "Conflito",
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(RegraNegocioException.class)

    public ResponseEntity<ApiError> tratarRegraNegocio(
        RegraNegocioException ex,
        HttpServletRequest request
    ){
        ApiError erro = new ApiError(
            LocalDateTime.now(), 
            400,
            "Bad request",
            
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
       
    @ExceptionHandler(AuthenticationException.class)
public ResponseEntity<ApiError> tratarErroAutenticacao(
        AuthenticationException ex,
        HttpServletRequest request) {

    ApiError erro = new ApiError(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            "Não autorizado",
            "E-mail ou senha inválidos.",
            request.getRequestURI(),
            List.of()
    );

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(erro);
}

}
