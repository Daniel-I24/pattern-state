package auth;

/**
 * Representa un usuario registrado en el sistema.
 * Contiene sus credenciales y el rol que determina sus permisos.
 */
public class User {

    private final String id;
    private final String nombre;
    private final String correo;
    private final String contrasena;
    private final Role rol;

    public User(String id, String nombre, String correo, String contrasena, Role rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getId()         { return id; }
    public String getNombre()     { return nombre; }
    public String getCorreo()     { return correo; }
    public String getContrasena() { return contrasena; }
    public Role getRol()          { return rol; }

    @Override
    public String toString() {
        return nombre + " (" + rol.getEtiqueta() + ")";
    }
}
