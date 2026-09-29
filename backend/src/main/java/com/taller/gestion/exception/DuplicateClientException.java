package com.taller.gestion.exception;

/** Señala que teléfono o correo ya pertenece a un cliente existente. */
public class DuplicateClientException extends RuntimeException {
  public DuplicateClientException(String field) { super("Ya existe un cliente con ese " + field + "."); }
}
