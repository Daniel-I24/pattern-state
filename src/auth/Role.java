package auth;

/**
 * Roles disponibles en el sistema.
 * Cada rol define qué acciones puede realizar el usuario.
 */
public enum Role {
    GERENTE("Gerente"),
    DESARROLLADOR("Desarrollador"),
    CLIENTE("Cliente");

    private final String etiqueta;

    Role(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
