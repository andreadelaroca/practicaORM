package ni.edu.uam.practicaorm.controller;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.practicaorm.model.Estudiante;
import ni.edu.uam.practicaorm.repository.EstudianteRepository;

import java.util.List;

public class EstudianteController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtCorreo;
    @FXML private ComboBox<String> cmbCarrera;
    @FXML private TextField txtTelefono;
    @FXML private Button btnGuardar;
    @FXML private Button btnConsultar;
    @FXML private Button btnFiltrar;

    @FXML private TableView<Estudiante> tablaEstudiantes;
    @FXML private TableColumn<Estudiante, Long> colId;
    @FXML private TableColumn<Estudiante, String> colNombres;
    @FXML private TableColumn<Estudiante, String> colApellidos;
    @FXML private TableColumn<Estudiante, String> colCorreo;
    @FXML private TableColumn<Estudiante, String> colCarrera;
    @FXML private TableColumn<Estudiante, String> colTelefono;

    private final EstudianteRepository repository = new EstudianteRepository();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("carrera"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        cmbCarrera.setItems(FXCollections.observableArrayList(
                "Ingeniería de Sistemas de Información",
                "Ingeniería Industrial",
                "Administración de Empresas",
                "Marketing y Negocios Internacionales",
                "Derecho"
        ));

        consultarEstudiantes();
    }

    @FXML
    public void guardarEstudiante() {
        String nombres = txtNombres.getText() != null ? txtNombres.getText().trim() : "";
        String apellidos = txtApellidos.getText() != null ? txtApellidos.getText().trim() : "";
        String correo = txtCorreo.getText() != null ? txtCorreo.getText().trim() : "";
        String carrera = cmbCarrera.getValue();
        String telefono = txtTelefono.getText() != null ? txtTelefono.getText().trim() : "";

        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || carrera == null || carrera.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación de Campos", "Todos los campos principales son obligatorios.");
            return;
        }

        if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Formato Inválido", "El correo ingresado no cumple con el formato estándar.");
            return;
        }

        setInteraccion(false);

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() {
                if (repository.existeCorreo(correo)) {
                    throw new IllegalArgumentException("El correo '" + correo + "' ya se encuentra registrado en el sistema.");
                }
                Estudiante nuevo = new Estudiante(nombres, apellidos, correo, carrera, telefono);
                repository.guardar(nuevo);
                return null;
            }
        };

        saveTask.setOnSucceeded(e -> {
            setInteraccion(true);
            limpiarCampos();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Estudiante registrado correctamente.");
            consultarEstudiantes();
        });

        saveTask.setOnFailed(e -> {
            setInteraccion(true);
            Throwable ex = saveTask.getException();
            if (ex instanceof IllegalArgumentException) {
                mostrarAlerta(Alert.AlertType.ERROR, "Correo Duplicado", ex.getMessage());
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Persistencia", "Fallo al guardar el registro: " + ex.getMessage());
            }
        });

        new Thread(saveTask).start();
    }

    @FXML
    public void consultarEstudiantes() {
        setInteraccion(false);

        Task<List<Estudiante>> queryTask = new Task<>() {
            @Override
            protected List<Estudiante> call() {
                return repository.listar();
            }
        };

        queryTask.setOnSucceeded(e -> {
            setInteraccion(true);
            tablaEstudiantes.setItems(FXCollections.observableArrayList(queryTask.getValue()));
        });

        queryTask.setOnFailed(e -> {
            setInteraccion(true);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo consultar los registros.");
        });

        new Thread(queryTask).start();
    }

    @FXML
    public void filtrarPorCarrera() {
        String carrera = cmbCarrera.getValue();
        if (carrera == null || carrera.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Filtro Requerido", "Seleccione una carrera en el ComboBox para filtrar.");
            return;
        }

        setInteraccion(false);

        Task<List<Estudiante>> filterTask = new Task<>() {
            @Override
            protected List<Estudiante> call() {
                return repository.buscarPorCarrera(carrera);
            }
        };

        filterTask.setOnSucceeded(e -> {
            setInteraccion(true);
            tablaEstudiantes.setItems(FXCollections.observableArrayList(filterTask.getValue()));
        });

        filterTask.setOnFailed(e -> {
            setInteraccion(true);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Fallo al ejecutar el filtrado HQL.");
        });

        new Thread(filterTask).start();
    }

    private void limpiarCampos() {
        txtNombres.clear();
        txtApellidos.clear();
        txtCorreo.clear();
        cmbCarrera.setValue(null);
        txtTelefono.clear();
    }

    private void setInteraccion(boolean enabled) {
        btnGuardar.setDisable(!enabled);
        btnConsultar.setDisable(!enabled);
        btnFiltrar.setDisable(!enabled);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}