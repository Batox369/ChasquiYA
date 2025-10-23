package app.infrastructure.shared;

import app.domain.model.Usuario;
import app.Main;
import java.util.prefs.Preferences;

public class SessionManager {

    // 1. Obtenemos el "nodo" de preferencias para tu app
    private static final Preferences prefs =
            Preferences.userNodeForPackage(Main.class);

    // 2. Esta es la "llave" para guardar el dato
    private static final String USERNAME_KEY = "LOGGED_IN_USER";

    // 3. Objeto para guardar el usuario actual EN MEMORIA (mientras la app corre)
    private static Usuario currentUser = null;


    /**
     * Guarda el username en las Preferences del SO (Función "Recordarme").
     */
    public static void saveSession(String username) {
        if (username == null) {
            return;
        }
        System.out.println("Guardando sesión persistente para: " + username);
        prefs.put(USERNAME_KEY, username);
    }

    /**
     * Borra la sesión persistente de las Preferences (al cerrar sesión).
     */
    public static void clearSession() {
        System.out.println("Borrando sesión persistente.");
        prefs.remove(USERNAME_KEY);
        currentUser = null;
    }

    /**
     * Carga el username guardado al iniciar la app.
     * @return El username si existe, o null si no hay sesión guardada.
     */
    public static String getSavedUsername() {
        return prefs.get(USERNAME_KEY, null);
    }

    // --- Métodos para manejar la sesión en memoria ---

    /**
     * Guarda el objeto Usuario completo en memoria después de validar.
     */
    public static void setCurrentUser(Usuario user) {
        currentUser = user;
    }

    /**
     * Obtiene el usuario que está actualmente logueado en la app.
     * @return El objeto Usuario, o null si nadie está logueado.
     */
    public static Usuario getCurrentUser() {
        return currentUser;
    }
}
