package fr.zenika.pokecombat.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class GestionDesErreurs {

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    ProblemDetail inconnu(HttpClientErrorException.NotFound e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Pokémon ou attaque inconnu de PokeAPI.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail demandeInvalide(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ResourceAccessException.class)
    ProblemDetail pokeApiInjoignable(ResourceAccessException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "PokeAPI est injoignable : " + e.getMessage());
    }
}
