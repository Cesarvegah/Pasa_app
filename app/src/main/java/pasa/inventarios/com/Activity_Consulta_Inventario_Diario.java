package pasa.inventarios.com;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pasa.inventarios.com.Contrato.*;


public class Activity_Consulta_Inventario_Diario extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener  {

    String str_id = "";
    TextView str_divisionTV;
    EditText str_fechaET;
    String str_division = "";
    String str_fecha = "";
    String str_user = "";
    String str_barcode = "";
    String str_folio = "";
    String str_id_almacen = "";
    String str_cerrar = "";
    String str_branch = "";
    String str_folio_cerrado = "";
    View addView;
    TextView txtViewTit;
    TextView txtViewSub;
    ViewGroup finalContainer = null;
    Button btn_Sincronizar;
    Button btn_delete_item;
    ImageButton btn_search;
    Button btn_show_all;
    LinearLayout container;
    boolean result1 = false;
    boolean val11 = true;
    boolean val12 = true;
    boolean val13 = true;
    boolean val14 = true;
    boolean result = false;
    boolean val1 = false;
    boolean val2 = false;
    boolean val3 = false;
    boolean val4 = false;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private AdaptadorInventarios adaptador;
    boolean bool_query_inv = false;
    boolean bool_query_inv_diario = false;
    DbDataSource dataSource;
    TextView txtViewFolio_Inv;
    SimpleCursorAdapter genreSpinnerAdapter2;
    Spinner spn_Almacen;
    String str_Equipo_Almacen = "";
    String str_Folio_Inventario_Diario = "";
    int id_Branch;
    int id_Equipo_Almacen;

    HelperInventarios baseDatos;

    private static Activity_Consulta_Inventario_Diario instancia = new Activity_Consulta_Inventario_Diario();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    public Activity_Consulta_Inventario_Diario() {
    }

    public Activity_Consulta_Inventario_Diario obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new HelperInventarios(contexto);
        }
        return instancia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__consulta__inventario__diario);

        obtenerInstancia(getApplicationContext());

        dataSource = new DbDataSource(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        setTitle(R.string.consulta_inventario_diario);
        container = (LinearLayout) findViewById(R.id.lin_AddEditTextEscaner);
        str_divisionTV = (TextView) findViewById(R.id.txtView_Division);

        txtViewFolio_Inv = (TextView) findViewById(R.id.txtView_Folio);

        dateFormatter = new SimpleDateFormat("dd/MMMM/yyyy", Locale.US);
        str_fechaET = (EditText) findViewById(R.id.editText_Fecha);
        str_fechaET.setInputType(InputType.TYPE_NULL);
        str_fechaET.requestFocus();
        str_fechaET.setOnClickListener(this);
        setDateTimeField();
        finalContainer = container;
        if (finalContainer.getChildCount() != 0) {
            finalContainer.removeAllViews();
        }

        spn_Almacen = (Spinner) findViewById(R.id.spn_Almacen);

        genreSpinnerAdapter2 = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,//Layout simple
                dataSource.getCatAlmacenes(),//Todos los registros
                new String[]{Contrato.cls_Columnas_Catalogo_Almacenes.
                        STR_EQUIPO_ALMACEN_DESCRIPCION},//Mostrar solo el nombre
                new int[]{android.R.id.text1},//View para el nombre
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco
        spn_Almacen.setAdapter(genreSpinnerAdapter2);
        spn_Almacen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor colCur=(Cursor)spn_Almacen.getSelectedItem();
                str_Folio_Inventario_Diario = colCur.getString(colCur.getColumnIndex(
                        Contrato.cls_Columnas_Catalogo_Almacenes.STR_FOLIO_INVENTARIO_DIARIO));
                String str_Bran = colCur.getString(colCur.getColumnIndex(Contrato.
                        cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID));
                str_Equipo_Almacen = str_Folio_Inventario_Diario;
                txtViewFolio_Inv.setText(str_Folio_Inventario_Diario);
                id_Branch = Integer.parseInt(str_Bran);
                id_Equipo_Almacen = (int) id;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_Sincronizar = (Button) findViewById(R.id.btn_Sincronizacion);
        btn_Sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!estaConectado()) {
                } else {
                    TareaWSInsertarInventarioDiario tare = new TareaWSInsertarInventarioDiario();
                    tare.execute();
                    prepararLista();
                }
                /*  Fin validacion del internet    */

            }
        });

        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Elije una fecha" + str_fechaET.getText().toString(), Toast.LENGTH_SHORT).show();
                if (txtViewFolio_Inv.getText().toString().length() != 0) {
                    if (str_fechaET.getText().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Elije una fecha", Toast.LENGTH_SHORT).show();
                    } else {
                        mtd_Query_date();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Elije un almacén que cuente con un folio", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_show_all = (Button) findViewById(R.id.btn_mostrar);
        btn_show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Hola", Toast.LENGTH_SHORT).show();
                str_fechaET.setText("");
                prepararLista();
            }
        });

        mtd_Query_Tbl_Login_User();
        prepararLista();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void mtd_Query_date() {
        finalContainer.removeAllViews();
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO + " where vch_fecha = " + "'" + str_fechaET.getText().toString() + "' and vch_folio_inventario_diario = '" + txtViewFolio_Inv.getText().toString() + "'", null);// vch_fecha
        if (c.moveToFirst()) {
            do {
                str_id = c.getString(0);
                str_division = c.getString(1);
                str_fecha = c.getString(2);
                str_user = c.getString(3);
                str_barcode = c.getString(4);
                str_branch = c.getString(5);
                Log.i("", "==>>     " + str_id + "--" + str_division + "--" + str_fecha + "--" + str_user + "--" + str_barcode + "--" + str_branch);
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                addView = layoutInflater.inflate(R.layout.listitem_titular, null);
                txtViewTit = (TextView) addView.findViewById(R.id.LblTitulo);
                txtViewTit.setText(str_barcode);
                txtViewSub = (TextView) addView.findViewById(R.id.LblSubTitulo);

                txtViewSub.setText(str_user + " -- " + c.getString(5));
                btn_delete_item = (Button) addView.findViewById(R.id.btn_delete_item);
                final String bar = txtViewTit.getText().toString();
                btn_delete_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "El código " + bar + " se eliminó correctamente", Toast.LENGTH_SHORT).show();
                        db.execSQL("DELETE FROM " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO + " WHERE " + Contrato.cls_Columnas_Inventario_Diario.STR_BARCODE + " = '" + bar + "'");
                        prepararLista();
                    }
                });
                finalContainer.addView(addView);
                str_divisionTV.setText(c.getString(1));
            } while (c.moveToNext());
            btn_Sincronizar.setBackgroundColor(Color.parseColor("#017A42"));
            btn_Sincronizar.setEnabled(true);
        } else {
            btn_Sincronizar.setBackgroundColor(Color.parseColor("#60000000"));
            btn_Sincronizar.setEnabled(false);
            Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT).show();
        }
    }

    public void mtd_Query_Tbl_Login_User() {
        Log.i("=========>>>>>>>>", "  Soy el metodo mtd_Query_Tbl_Login_User");
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_LOGIN_USER + " where int_select = 1", null);
        if (c.moveToFirst()) {
            String str_pass = "";
            String str_app = "";
            String str_valida = "";
            String str_bandera = "";
            do {
                str_branch = c.getString(0);
                str_user = c.getString(1);
                str_pass = c.getString(2);
                str_app = c.getString(3);
                str_division = c.getString(4);
                str_valida = c.getString(5);
                str_bandera = c.getString(6);
                Log.i("", "==>>     " + str_branch + "--" + str_user + "--" + str_pass + "--" + str_app + "--" + str_division + "--" + str_valida + "--" + str_bandera);
                str_divisionTV.setText(str_division);
            } while (c.moveToNext());
        }
    }

    private void setDateTimeField() {
        str_fechaET.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(Activity_Consulta_Inventario_Diario.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                SimpleDateFormat df = new SimpleDateFormat("dd/MMMM/yyyy");
                String formattedDate = df.format(newDate.getTime());

                str_fechaET.setText(formattedDate);
                //str_fechaET.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void prepararLista() {
        finalContainer.removeAllViews();
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null);
        if (c.moveToFirst()) {
            do {
                str_id = c.getString(0);
                str_division = c.getString(1);
                str_fecha = c.getString(2);
                str_user = c.getString(3);
                str_barcode = c.getString(4);
                str_branch = c.getString(6);
                Log.i("", str_id + "--" + str_division + "--" + str_fecha + "--" + str_user + "--" + str_barcode + c.getString(5) + "--" + str_branch);
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                addView = layoutInflater.inflate(R.layout.listitem_titular, null);
                txtViewTit = (TextView) addView.findViewById(R.id.LblTitulo);
                txtViewTit.setText(str_barcode);
                txtViewSub = (TextView) addView.findViewById(R.id.LblSubTitulo);
                txtViewSub.setText(str_user + " -- " + c.getString(5));
                btn_delete_item = (Button) addView.findViewById(R.id.btn_delete_item);
                final String bar = txtViewTit.getText().toString();
                btn_delete_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "El código " + bar + " se eliminó correctamente", Toast.LENGTH_SHORT).show();
                        db.execSQL("DELETE FROM " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO + " WHERE " + Contrato.cls_Columnas_Inventario_Diario.STR_BARCODE + " = '" + bar + "'");
                        prepararLista();
                    }
                });
                finalContainer.addView(addView);
                str_divisionTV.setText(c.getString(1));
            } while (c.moveToNext());

            btn_Sincronizar.setBackgroundColor(Color.parseColor("#017A42"));
            btn_Sincronizar.setEnabled(true);
        } else {
            btn_Sincronizar.setBackgroundColor(Color.parseColor("#60000000"));
            btn_Sincronizar.setEnabled(false);
            Toast.makeText(getApplicationContext(), "No hay registros", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insercion_contacto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sincronisar:
                //Toast.makeText(getApplicationContext(), "Prueba Consulta", Toast.LENGTH_SHORT).show();
                if (!estaConectado()) {
                } else {
                    if(finalContainer.getChildCount() != 0){
                        //Toast.makeText(getApplicationContext(), "Hay " + finalContainer.getChildCount(), Toast.LENGTH_SHORT).show();
                        TareaWSInsertarInventarioDiario tare = new TareaWSInsertarInventarioDiario();
                        tare.execute();
                    }else{
                        //Toast.makeText(getApplicationContext(), "No hay", Toast.LENGTH_SHORT).show();
                        prepararLista();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.editText_Fecha){
            fromDatePickerDialog.show();
        }

        if(v.getId() == R.id.editText_Fecha){
            fromDatePickerDialog.show();
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            Intent i = new Intent(Activity_Consulta_Inventario_Diario.this,Activity_Home.class);
            startActivity(i);
        }

        if (id == R.id.nav_Agregar) {
            Intent i = new Intent(Activity_Consulta_Inventario_Diario.this,Activity_AddData.class);
            startActivity(i);
        }

        if (id == R.id.nav_InventarioDiario) {
            Intent i = new Intent(Activity_Consulta_Inventario_Diario.this,Activity_Inventario_Diario.class);
            startActivity(i);
        }

        if (id == R.id.nav_ConsultaInventarioDiario) {
            Intent i = new Intent(Activity_Consulta_Inventario_Diario.this,Activity_Consulta_Inventario_Diario.class);
            startActivity(i);
        }

        if (id == R.id.nav_Sincronizar) {
            Intent i = new Intent(Activity_Consulta_Inventario_Diario.this,Actividad_Lista_Inventarios.class);
            startActivity(i);
        }

        if (id == R.id.nav_Salir) {
            boolean bool_query_inv1 = mtd_consulta_inventario();
            boolean bool_query_inv_diario1 = mtd_consulta_tbl_inventario_diario();

            //Toast.makeText(getApplicationContext(), "Datos " + bool_query_inv1 + bool_query_inv_diario1, Toast.LENGTH_SHORT).show();
            Log.i("", "Datos " + bool_query_inv1 + bool_query_inv_diario1);
            if(bool_query_inv1 || bool_query_inv_diario1){
                mtd_alert_dialog(bool_query_inv1, bool_query_inv_diario1);
            }else{
                mtd_salir_sesion();
            }
        }
        return true;
    }

    /*
        @Override
        public void onStart() {
            super.onStart();

            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client.connect();
            Action viewAction = Action.newAction(
                    Action.TYPE_VIEW, // TODO: choose an action type.
                    "Activity_Consulta_Inventario_Diario Page", // TODO: Define a title for the content shown.
                    // TODO: If you have web page content that matches this app activity's content,
                    // make sure this auto-generated web page URL is correct.
                    // Otherwise, set the URL to null.
                    Uri.parse("http://host/path"),
                    // TODO: Make sure this auto-generated app URL is correct.
                    Uri.parse("android-app://pasa.inventarios.com/http/host/path")
            );
            AppIndex.AppIndexApi.start(client, viewAction);
        }

        @Override
        public void onStop() {
            super.onStop();

            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            Action viewAction = Action.newAction(
                    Action.TYPE_VIEW, // TODO: choose an action type.
                    "Activity_Consulta_Inventario_Diario Page", // TODO: Define a title for the content shown.
                    // TODO: If you have web page content that matches this app activity's content,
                    // make sure this auto-generated web page URL is correct.
                    // Otherwise, set the URL to null.
                    Uri.parse("http://host/path"),
                    // TODO: Make sure this auto-generated app URL is correct.
                    Uri.parse("android-app://pasa.inventarios.com/http/host/path")
            );
            AppIndex.AppIndexApi.end(client, viewAction);
            client.disconnect();
        }
    */
    private class TareaWSInsertarInventarioDiario extends AsyncTask<String, Integer, Boolean> {
        protected Boolean doInBackground(String... params) {
            String message;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquiposInsertReporte");
            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            try {
                Log.i("=========>>>>>>>>", "  Soy el metodo TareaWSInsertar - Inventario Diario");
                SQLiteDatabase db = baseDatos.getWritableDatabase();
                Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null);
                db.execSQL("UPDATE tbl_inventario_diario SET cerrarInventario = 1 where _id = (SELECT MAX(_id) from tbl_inventario_diario)");
                if (c.moveToFirst()) {
                    LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    addView = layoutInflater.inflate(R.layout.listitem_titular, null);
                    JSONObject object = new JSONObject();
                    ContentValues valores = new ContentValues();
                    HttpResponse resp;
                    do {
                        str_id = c.getString(0);
                        str_division = c.getString(1);
                        str_fecha = c.getString(2);
                        str_user = c.getString(3);
                        str_barcode = c.getString(4);
                        str_folio = c.getString(5);
                        str_id_almacen = c.getString(6);
                        str_cerrar = c.getString(7);
                        str_branch = c.getString(8);

                        //13--GIN MONTERREY--25/julio/2016--4400170--hjjk--INV/EQUIP-000009-072016--9
                        Log.i("Muestra", "==>>     " + str_id + "--" + str_division + "--" + str_fecha + "--" + str_user + "--" + str_barcode + "--" + str_folio + "--" + str_id_almacen + "--" + str_cerrar + "--" + str_branch);
                        ////////////////////////////////////////////////
                        object.put("equipoFolio", str_barcode.trim());
                        object.put("branchId", str_branch.trim());
                        object.put("user", str_user.trim());
                        object.put("equipoAlmacenId", str_id_almacen.trim());
                        object.put("folioInventarioDiario", str_folio.trim());
                        object.put("cerrarInventario", str_cerrar);

                        message = object.toString();
                        post.setEntity(new StringEntity(message, "UTF8"));
                        post.setHeader("Content-type", "application/json");
                        resp = httpClient.execute(post);
                        if (resp != null) {
                            if (resp.getStatusLine().getStatusCode() == 204) {
                                result = true;
                                Log.i("=====>>>>>", " Soy IF ");
                            } else {
                                Log.i("=====>>>>>", " Soy ELSE " + resp.getStatusLine().getStatusCode());
                            }
                        }
                        String respuesta = "Respuesta default";
                        //assert resp != null;
                        respuesta = EntityUtils.toString(resp.getEntity());
                        //showToast(respuesta);
                        String[] str_validar_msj = respuesta.split(" ");
                        String str_last1 = str_validar_msj[0] + " " + str_validar_msj[1] + " " + str_validar_msj[2];
                        String str_last2 = str_validar_msj[str_validar_msj.length - 1];
                        Log.i("", " =====>>>>> :\"" + str_last1 + "\"");
                        Log.i("", " =====>>>>> :\"" + str_last2 + "\"");
                        if (str_last1.equals("\"Se registro Correctamente")) {
                            String[] args = new String[]{str_id};
                            db.execSQL("DELETE FROM tbl_inventario_diario WHERE _id=?", args);
                        } else {
                            val11 = false;
                            if (str_last2.equals("Existente\"")) {
                                val12 = false;
                            } else {
                                if (str_last2.equals("Cerrado\"")) {
                                    val14 = false;
                                    str_folio_cerrado = str_folio;
                                }else{
                                    val13 = false;
                                }
                            }
                        }

                        Log.i("getEntity.getContent", "=====>>>>> " + respuesta);
                        Log.i("Status", "=====>>>>> " + resp.getStatusLine().getStatusCode());
                        Log.i("Status message", "=====>>>>>" + message);
                        Log.i("resp.getStatusLine", "=====>>>>>" + resp.getStatusLine().toString());
                        result = true;
                    } while (c.moveToNext());
                } else {
                    Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT);
                }
            } catch (Exception ex) {
                Log.i("ServicioRest", "Error=============>>>>!", ex);
                Log.i("TareaWSInsertar: ", "catch(Exception ex)");
                result = false;
            }
            return result;
        }

        public void showToast(String toast) {
            toast = toast.substring(1, toast.length() - 1);
            final String finalToast = toast;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Activity_Consulta_Inventario_Diario.this, finalToast, Toast.LENGTH_SHORT).show();
                }
            });
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.i("onPostExecute", "=========" + finalContainer.getChildCount());
                btn_Sincronizar.setBackgroundColor(Color.parseColor("#60000000"));
                btn_Sincronizar.setEnabled(false);
            }
            //Toast.makeText(getApplicationContext(), "Entré al onPostExecute", Toast.LENGTH_SHORT).show();
            if (val11) {
                Toast.makeText(getApplicationContext(), "Todos los datos se sincronizaron de manera correcta", Toast.LENGTH_SHORT).show();
            } else {
                if (!val12) {
                    Toast.makeText(getApplicationContext(), "Folios existentes con el mismo branch y usuario", Toast.LENGTH_SHORT).show();
                } else {
                    if (!val13) {
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error, repórtelo con la empresa", Toast.LENGTH_LONG).show();
                    } else{
                        if(!val14) {
                            //Toast.makeText(getApplicationContext(), "Éste folio ya está cerrado", Toast.LENGTH_LONG).show();
                            //mtd_dialog_folio(str_folio_cerrado);
                            mtd_dialog_alert(str_folio_cerrado);

                        }
                    }
                }
            }
            prepararLista();
        }
    }


    /*  Inicio validacion del internet    */
    protected Boolean estaConectado(){
        if(conectadoWifi()){
            return true;
        }else{
            if(conectadoRedMovil()){
                return true;
            }else{
                showAlertDialog(this, "Revisa tu conexión a Internet",
                        "Tu Dispositivo no tiene Conexión a Internet.", false);
                return false;
            }
        }
    }
    /*  Fin validacion del internet    */

    /*  Inicio validacion del internet    */
    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    /*  Fin validacion del internet    */

    /*  Inicio validacion del internet    */
    protected Boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    /*  Fin validacion del internet    */

    /*  Inicio validacion del internet    */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);

        alertDialog.setMessage(message);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }
    /*  Fin validacion del internet    */

    private void mtd_salir_sesion() {

        SharedPreferences preferences = getSharedPreferences("sesion", 0);
        preferences.edit().remove("user").commit();
        preferences.edit().remove("pass").commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }else{
            Intent salida=new Intent( Intent.ACTION_MAIN); //Llamando a la activity principal
            finish(); // La cerramos.
        }
    }

    private void mtd_alert_dialog(final boolean bool_query_inv1, final boolean bool_query_inv_diario1) {
        final android.support.v7.app.AlertDialog.Builder dialogo1 = new android.support.v7.app.AlertDialog.Builder(this);
        dialogo1.setTitle("Advertencia... ");
        dialogo1.setMessage("Tienes registros que no se han sincronizado, ¿Deseas sincronizar antes de salir de tu sesión?");
        dialogo1.setIcon(R.drawable.ic_information_black_18dp);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                if(!estaConectado()) {
                }
                else {
                    mtd_sincronizar_y_salir(bool_query_inv1, bool_query_inv_diario1);
                    if(!val1 || val2 || val3 || val4 || !val11 || !val12 || !val13 || !val14) {
                        Toast.makeText(getApplicationContext(), "Hay registros que no se pudieron sincronizar", Toast.LENGTH_LONG).show();

                        val11 = true;
                        val12 = true;
                        val13 = true;
                        val14 = true;

                        val1 = false;
                        val2 = false;
                        val3 = false;
                        val4 = false;
                    }else {
                        mtd_salir_sesion();

                    }
                    //mtd_cerrar_sesion();
                }
                //Toast.makeText(getApplicationContext(), "Sincronizar y salir", Toast.LENGTH_LONG).show();
            }
        });
        dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                mtd_borrar_y_salir();
                mtd_salir_sesion();
            }
        });
        dialogo1.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Se canceló", Toast.LENGTH_LONG).show();
            }
        });
        dialogo1.show();
    }

    private void mtd_borrar_y_salir() {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        db.execSQL("delete from " + HelperInventarios.Tablas.INVENTARIO);
        db.execSQL("delete from " + HelperInventarios.Tablas.TBL_CATALOGO_TIPO_EQUIPO);
        db.execSQL("delete from " + HelperInventarios.Tablas.TBL_CATALOGO_ALMACENES);
        db.execSQL("delete from " + HelperInventarios.Tablas.TBL_LOGIN_USER);
        db.execSQL("delete from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO);
    }

    private void mtd_sincronizar_y_salir(boolean bool_query_inv1, boolean bool_query_inv_diario1) {
        if(bool_query_inv1){
            if (!estaConectado()) {
            } else {
                if (!estaConectado()) {
                } else {
                    TareaWSInsertarInventario tare = new TareaWSInsertarInventario();
                    tare.execute();
                }
            }
        }
        if(bool_query_inv_diario1){
            if (!estaConectado()) {
            } else {
                TareaWSInsertarInventarioDiario tare = new TareaWSInsertarInventarioDiario();
                tare.execute();
            }
        }
    }

    public boolean mtd_consulta_inventario() {
        Log.i("", " ==>> -------------------------     mtd_consulta_inventario");
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.INVENTARIO, null);
        if (c.moveToFirst()) {
            Log.i("", "==>>     " + c.getString(0) + "--" + c.getString(1) + "--" + c.getString(2) + "--" + c.getString(3) + "--" + c.getString(4) + "--" + c.getString(6) + "--" + c.getString(7) + "--" + c.getString(8));
            bool_query_inv = true;
            do {
            } while (c.moveToNext());
        } else {
            bool_query_inv = false;
        }
        return bool_query_inv;
    }

    public boolean mtd_consulta_tbl_inventario_diario() {
        Log.i("", "==>>-------------------------     mtd_consulta_tbl_inventario_diario");
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null);
        if (c.moveToFirst()) {
            Log.i("", "==>>     " + c.getString(0) + "--" + c.getString(1) + "--"
                    + c.getString(2) + "--" + c.getString(3) + "--" + c.getString(4)
                    + "--" + c.getString(5));
            bool_query_inv_diario = true;
            do {
            } while (c.moveToNext());
        } else {
            bool_query_inv_diario = false;
        }
        return bool_query_inv_diario;
    }

    public void mtd_cerrar_sesion(){
        Toast.makeText(getApplicationContext(), "Sesión cerrada con éxito", Toast.LENGTH_LONG).show();
        SharedPreferences preferences = getSharedPreferences("sesion", 0);
        preferences.edit().remove("user").commit();
        preferences.edit().remove("pass").commit();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }




    private class TareaWSInsertarInventario extends AsyncTask<String, Integer, Boolean> {
        public int contador = 0;

        protected Boolean doInBackground(String... params) {
            String message;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquipos");
            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            //post.setHeader("content-type", "application/json");
            try {
                String str_equipo_folio = "";
                String str_equipo_RFID = "";
                String str_tipo_Equipo_Id = "";
                String str_equipo_Almacen_Id = "";
                String str_equipo_Estatus_Id = "";
                String str_equipo_Propio = "";
                String str_branch_Id = "";
                SQLiteDatabase db = baseDatos.getWritableDatabase();
                Cursor c = db.rawQuery("Select equipo_Folio, equipo_RFID, tipo_Equipo_Id, equipo_Almacen_Id, equipo_Estatus_Id, equipo_Propio, branch_Id from " + HelperInventarios.Tablas.INVENTARIO, null);
                if (c.moveToFirst()) {
                    JSONObject object = new JSONObject();
                    ContentValues valores = new ContentValues();
                    HttpResponse resp;
                    do {
                        str_equipo_folio = c.getString(0);
                        str_equipo_RFID = c.getString(1);
                        str_tipo_Equipo_Id = c.getString(2);
                        str_equipo_Almacen_Id = c.getString(3);
                        str_equipo_Estatus_Id = c.getString(4);
                        str_equipo_Propio = c.getString(5);
                        str_branch_Id = c.getString(6);
                        object.put("equipoFolio", str_equipo_folio);
                        object.put("equipoRFID", "");
                        object.put("tipoEquipoId", str_tipo_Equipo_Id);
                        object.put("equipoAlmacenId", str_equipo_Almacen_Id);
                        object.put("equipoEstatusId", str_equipo_Estatus_Id);
                        object.put("equipoPropio", str_equipo_Propio);
                        object.put("branchId", str_branch_Id);
                        message = object.toString();
                        post.setEntity(new StringEntity(message, "UTF8"));
                        post.setHeader("Content-type", "application/json");
                        resp = httpClient.execute(post);
                        if (resp != null) {
                            if (resp.getStatusLine().getStatusCode() == 204)
                                result = true;
                        }
                        String respuesta = EntityUtils.toString(resp.getEntity());
                        String[] str_validar_msj = respuesta.split(" ");
                        String str_last2 = str_validar_msj[str_validar_msj.length - 1];
                        if (str_last2.equals("correctamente\"")) {
                            Log.e("Mensaje: correctamente", str_last2);
                            //db.execSQL("DELETE FROM inventario WHERE equipo_Folio = '" + str_equipo_folio + "'", null);
                            db.execSQL("DELETE FROM " + HelperInventarios.Tablas.INVENTARIO + " WHERE " + Contrato.ColumnasPasa.EQUIPO_FOLIO + " = '" + str_equipo_folio + "'");
                            val1 = true;
                        } else {
                            val1 = false;
                            if (str_last2.equals("registrado\"")) {
                                Log.e("Mensaje: registrado", str_last2);
                                val2 = true;
                            } else {
                                if(str_last2.equals("Existe\"")){
                                    val4 = true;
                                }else {
                                    Log.e("Mensaje: error", str_last2);
                                    val3 = true;
                                }
                            }
                        }
                        result = true;
                    } while (c.moveToNext());
                } else {
                    Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT);
                }
            } catch (Exception ex) {
                Log.i("ServicioRest", "Error=============>>>>!", ex);
                Log.i("TareaWSInsertar: ", "catch(Exception ex)");
                result = false;
            }
            return result;
        }

        public void showToast(String toast) {
            toast = toast.substring(1, toast.length() - 1);
            final String finalToast = toast;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Activity_Consulta_Inventario_Diario.this, finalToast, Toast.LENGTH_SHORT).show();
                }
            });
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
            }
            if (val1) {
                Toast.makeText(getApplicationContext(), "Todos los datos se sincronizaron de manera correcta", Toast.LENGTH_LONG).show();
            } else {
                if (val2) {
                    Toast.makeText(getApplicationContext(), "Folios existentes con el mismo branch y usuario", Toast.LENGTH_LONG).show();
                } else {
                    if(val4){
                        Toast.makeText(getApplicationContext(), "El almacén no existe en la base de datos", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error, repórtelo con la empresa", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }




    public void mtd_dialog_alert(final String str_folio_cerrado){
        final android.support.v7.app.AlertDialog.Builder dialogo1 = new android.support.v7.app.AlertDialog.Builder(this);
        dialogo1.setTitle("Advertencia... ");
        dialogo1.setMessage("El folio de inventario: " + str_folio_cerrado + " ya está cerrado, los registros que contienen este folio necesitan ser cambiados por otro folio para realizar la sincronización correcta");
        dialogo1.setIcon(R.drawable.ic_information_black_18dp);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                mtd_dialog_elije(str_folio_cerrado);
            }
        });
        dialogo1.show();
    }


    public void mtd_dialog_elije(final String str_folio_cerrado){

        int i = 0;
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("select vch_folio_inventario_diario from " + HelperInventarios.Tablas.TBL_CATALOGO_ALMACENES + " where vch_folio_inventario_diario <> '' and vch_folio_inventario_diario <> '" + str_folio_cerrado + "'", null);
        final String[] items = new String[c.getCount()];
        if(c.moveToFirst()){
            do {
                items[i] = c.getString(0);
                Log.i("", items[i]);
                i++;
            }while (c.moveToNext());
        }
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Activity_Consulta_Inventario_Diario.this);
        builder.setTitle("Elije el nuevo folio:");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(), "El nuevo folio es: \n" + items[item], Toast.LENGTH_LONG).show();
                mtd_update_folio_cerrado(str_folio_cerrado, items[item]);
            }
        });
        builder.show();
    }
    public void mtd_update_folio_cerrado(String str_folio_cerrado, String item){
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.clear();
        db.execSQL("UPDATE "+ HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO +" SET cerrarInventario = " + 0);
        db.execSQL("UPDATE "+ HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO
                +" SET vch_folio_inventario_diario = '" + item
                + "', equipoAlmacenId = (select " + cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID + " from "
                + HelperInventarios.Tablas.TBL_CATALOGO_ALMACENES
                + " where " + cls_Columnas_Catalogo_Almacenes.STR_FOLIO_INVENTARIO_DIARIO + " = '" + item
                + "') where vch_folio_inventario_diario = '"
                + str_folio_cerrado + "'");

        //equipoAlmacenId
        prepararLista();
    }
}
