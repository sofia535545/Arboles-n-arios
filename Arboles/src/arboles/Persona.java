package arboles;
//importaciones a usar

import java.time.LocalDate;

public class Persona {

    //atributos 
    private String Nombre;
    private int cedula;
    private LocalDate fechaNacimiento;

    //constructor 
    public Persona(String Nombre, int cedula, LocalDate fechaNacimiento) {
        this.Nombre = Nombre;
        this.cedula = cedula;
        this.fechaNacimiento = fechaNacimiento;
    }

    //getter and setter 
    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public int getCedula() {
        return cedula;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    //
}
