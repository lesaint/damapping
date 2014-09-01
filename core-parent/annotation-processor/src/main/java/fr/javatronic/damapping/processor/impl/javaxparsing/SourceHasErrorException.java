package fr.javatronic.damapping.processor.impl.javaxparsing;

import javax.lang.model.type.TypeMirror;

/**
 * SourceHasErrorException - Exception thrown during parsing when a {@link javax.lang.model.type.TypeMirror} with
 * kind {@link javax.lang.model.type.TypeKind#ERROR} is encountered during parsing.
 * <p>
 * This {@code TypeKind} constant indicates that the compiler did not fully resolve the Type.
 * </p>
 *
 * @author Sébastien Lesaint
 */
class SourceHasErrorException extends RuntimeException {
  private final TypeMirror typeMirror;

  SourceHasErrorException(TypeMirror typeMirror) {
    this.typeMirror = typeMirror;
  }

  SourceHasErrorException(String message, TypeMirror typeMirror) {
    super(message);
    this.typeMirror = typeMirror;
  }

  SourceHasErrorException(String message, Throwable cause, TypeMirror typeMirror) {
    super(message, cause);
    this.typeMirror = typeMirror;
  }

  SourceHasErrorException(Throwable cause, TypeMirror typeMirror) {
    super(cause);
    this.typeMirror = typeMirror;
  }

  SourceHasErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                          TypeMirror typeMirror) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.typeMirror = typeMirror;
  }

  TypeMirror getTypeMirror() {
    return typeMirror;
  }
}
