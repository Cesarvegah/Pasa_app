package pasa.inventarios.com;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.UUID;

/**
 * Created by Abraham on 27/06/2016.
 */
public class Contrato {
    interface ColumnasSincronizacion {
        String MODIFICADO = "modificado";
        String ELIMINADO = "eliminado";
        String INSERTADO = "insertado";
    }

    interface Columnas_Login_User {
        //String ID_INT_BRANCHID = "id_int_branch";// Pk
        String ID_INT_BRANCHID = "_id";// Pk
        String INT_USER = "int_user";
        String STR_PASS = "vrc_pass";
        String STR_APP = "vch_app";
        String STR_NAME = "vch_name";
        String INT_VALIDA = "int_valida";
        String INT_SELECT = "int_select";
    }
    interface Columnas_Catalogo_Almacenes {
        String ID = "_id";              // Pk
        String ID_INT_EQUIPO_ALMACEN_ID = "id_int_equipo_almacen";
        String STR_EQUIPO_ALMACEN_CLAVE = "vch_equipo_almacen_clave";
        String STR_EQUIPO_ALMACEN_DESCRIPCION = "vch_equipo_almacen_descripcion";
        String STR_FOLIO_INVENTARIO_DIARIO = "vch_folio_inventario_diario";
        String FK_INT_BRANCHID = "fk_int_branch";
    }
    interface Columnas_Catalogo_Tipo_Equipo {
        //String ID_INT_TIPO_EQUIPO_ID = "id_int_tipo_equipo";
        String ID_INT_TIPO_EQUIPO_ID = "_id";                 // Pk
        String STR_TIPO_EQUIPO_CLAVE = "vch_tipo_equipo_clave";
        String STR_TIPO_EQUIPO_DESCRIPCION = "vch_tipo_equipo_descripcion";
        String STR_TIPO_EQUIPO_CAPACIDAD = "vch_tipo_equipo_capacidad";
        String STR_TIPO_EQUIPO_UNIDAD_MEDIDA = "vch_tipo_equipo_unidad_medida";
        String STR_TIPO_EQUIPO_MOVIMIENTO = "vch_tipo_equipo_movimiento";
    }
    interface ColumnasPasa {
        //String ID_PASA = "id_Pasa";
        String ID_PASA = "_id";                         // Pk
        String EQUIPO_FOLIO = "equipo_Folio";
        String EQUIPO_RFID = "equipo_RFID";
        String FK_TIPO_EQUIPO_ID = "tipo_Equipo_Id";           // FK
        String FK_EQUIPO_ALMACEN_ID = "equipo_Almacen_Id";     // FK
        String EQUIPO_ESTATUS_ID = "equipo_Estatus_Id";
        String EQUIPO_PROPIO = "equipo_Propio";
        String FK_BRANCH_ID = "branch_Id";
        String EQUIPO_ALMACEN_STR = "equipo_Almacen_Str";
        String TIPO_EQUIPO_STR = "tipoEquipo";
    }
    interface Columnas_Inventario_Diario {
        String ID_INVENTARIO_DIARIO = "_id";                         // Pk
        String STR_DIVISION = "vch_division";
        String STR_FECHA = "vch_fecha";
        String STR_USER = "vch_user";
        String STR_BARCODE = "vch_barcode";
        String STR_FOLIO_INVENTARIO_DIARIO = "vch_folio_inventario_diario";
        String INT_EQUIPO_ALMACEN_ID = "equipoAlmacenId";
        String INT_cerrarInventario= "cerrarInventario";
        String INT_FK_ID = "fk_id";
    }
    // Autoridad del Content Provider
    public final static String AUTORIDAD = "pasa.inventarios.com";
    // Uri base
    public final static Uri URI_CONTENIDO_BASE = Uri.parse("content://" + AUTORIDAD);
    /**
     * Controlador de la tabla "contacto"
     */
    public static class Inventarios
            implements BaseColumns, ColumnasPasa, ColumnasSincronizacion {
        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_INVENTARIO).build();
        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_INVENTARIO;
        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_INVENTARIO;
        /**
         * Construye una {@link Uri} para el {@link #ID_PASA} solicitado.
         */
        public static Uri construirUriContacto(String idContacto) {
            return URI_CONTENIDO.buildUpon().appendPath(idContacto).build();
        }
        public static String generarIdContacto() {
            return "C-" + UUID.randomUUID();
        }
        public static String obtenerIdContacto(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    public static class cls_Columnas_Catalogo_Tipo_Equipo
            implements BaseColumns, Columnas_Catalogo_Tipo_Equipo, ColumnasSincronizacion {
        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_TIPO_EQUIPO).build();
        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_TIPO_EQUIPO;
        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_TIPO_EQUIPO;
        public static Uri construirUriContacto(String idContacto) {
            return URI_CONTENIDO.buildUpon().appendPath(idContacto).build();
        }
        public static String generarIdContacto() {
            return "C-" + UUID.randomUUID();
        }
        public static String obtenerIdContacto(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    public static class cls_Columnas_Catalogo_Almacenes
            implements BaseColumns, Columnas_Catalogo_Almacenes,  ColumnasSincronizacion {
        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_CATALOGO_ALMACENES).build();
        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_CATALOGO_ALMACENES;
        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_CATALOGO_ALMACENES;
        public static Uri construirUriContacto(String idContacto) {
            return URI_CONTENIDO.buildUpon().appendPath(idContacto).build();
        }
        public static String generarIdContacto() {
            return "C-" + UUID.randomUUID();
        }
        public static String obtenerIdContacto(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    public static class cls_Columnas_Login_User
            implements BaseColumns, Columnas_Login_User,  ColumnasSincronizacion {
        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_LOGIN_USER).build();
        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_LOGIN_USER;
        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_LOGIN_USER;
        public static Uri construirUriContacto(String idContacto) {
            return URI_CONTENIDO.buildUpon().appendPath(idContacto).build();
        }
        public static String generarIdContacto() {
            return "C-" + UUID.randomUUID();
        }
        public static String obtenerIdContacto(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    public static class cls_Columnas_Inventario_Diario
            implements BaseColumns, Columnas_Inventario_Diario,  ColumnasSincronizacion {
        public static final Uri URI_CONTENIDO =
                URI_CONTENIDO_BASE.buildUpon().appendPath(RECURSO_INVENTARIO_DIARIO).build();
        public final static String MIME_RECURSO =
                "vnd.android.cursor.item/vnd." + AUTORIDAD + "/" + RECURSO_INVENTARIO_DIARIO;
        public final static String MIME_COLECCION =
                "vnd.android.cursor.dir/vnd." + AUTORIDAD + "/" + RECURSO_INVENTARIO_DIARIO;
        public static Uri construirUriContacto(String idContacto) {
            return URI_CONTENIDO.buildUpon().appendPath(idContacto).build();
        }
        public static String generarIdContacto() {
            return "C-" + UUID.randomUUID();
        }
        public static String obtenerIdContacto(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
    // Recursos
    public final static String RECURSO_INVENTARIO = "inventarios";
    public final static String RECURSO_LOGIN_USER = "login_user";
    public final static String RECURSO_CATALOGO_ALMACENES = "catalogo_almacenes";
    public final static String RECURSO_TIPO_EQUIPO = "tipo_equipo";
    public final static String RECURSO_INVENTARIO_DIARIO = "tbl_inventario_diario";
}