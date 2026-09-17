package arboles;

import javax.swing.JOptionPane;

public class Arboles {

    static ArbolGenealogico arbol = new ArbolGenealogico();

    public static void main(String[] args) {
        //do while para el manejo de los menus
        do {
            //================= MENU PRINCIPAL =================
            int opc = MenuPrincipal();
            switch (opc) {
                case 1:
                    //================= MENU GESTION DE PERSONAS =================
                    boolean regresarPersona = false;

                    while (regresarPersona == false) {
                        int opc2 = MenuPersona();

                        switch (opc2) {
                            case 1:
                                arbol.pedirDatos();

                                break;
                            case 2:
                                int cedula = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                arbol.EliminarInfo(cedula);
                                break;
                            case 3:
                                int cedulaActualizar = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                arbol.Actualizar(cedulaActualizar);
                                break;
                            case 0:
                                regresarPersona = true;
                                break;

                            default:
                                System.out.println("opcion incorrecta");
                        }
                    }
                    break;

                case 2:
                    //================= MENU CONSULTAS FAMILIARES =================
                    if (arbol.getRaiz() == null) {

                        JOptionPane.showMessageDialog(null, "Arbol vacio");
                    } else {
                        boolean regresarConsultas = false;

                        while (regresarConsultas == false) {
                            int opc3 = MenuRelaciones();

                            switch (opc3) {
                                case 1:
                                    int cedulaPadre = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                    arbol.mostrarPadre(cedulaPadre);
                                    break;
                                case 2:
                                    int cedulaHijos = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                    arbol.buscarHijo(arbol.getRaiz(), cedulaHijos);
                                    break;
                                case 3:
                                    int cedulaHermanos = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                    arbol.mostrarHermanos(cedulaHermanos);
                                    break;

                                case 4:
                                    int cedulaTios = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                    arbol.mostrarTios(cedulaTios);
                                    break;

                                case 5:
                                    int cedulaSobrinos = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                    arbol.mostrarSobrinos(cedulaSobrinos);
                                    break;

                                case 6:
                                    int cedulaPrimos = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                    arbol.mostrarPrimos(cedulaPrimos);
                                    break;

                                case 7:
                                    int cedulaAncestros = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                    arbol.mostrarAncestros(cedulaAncestros);
                                    break;
                                case 8:
                                    int cedulaDescendientes = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                    arbol.mostrarDescendientes(cedulaDescendientes);
                                    break;
                                case 0:
                                    regresarConsultas = true;
                                    break;

                                default:
                                    System.out.println("opcion incorrecta");
                            }
                        }
                    }
                    break;

                case 3:
                    //================= MENU CONSULTAS ESTRUCTURALES =================
                    if (arbol.getRaiz() == null) {
                        JOptionPane.showMessageDialog(null, "Arbol vacio");
                    } else {
                        boolean regresarConsultas2 = false;

                        while (regresarConsultas2 == false) {
                            int opc4 = MenuEstructura();

                            switch (opc4) {
                                case 1:
                                    ventanaArbol ventana = new ventanaArbol(arbol.getRaiz());
                                    ventana.setVisible(true);
                                    break;
                                case 2:
                                    arbol.mostrarGrado();
                                    break;
                                case 3:
                                    arbol.mostrarMenor();
                                    break;
                                case 4:
                                    arbol.mostarNivel();
                                    break;

                                case 5:
                                    int cedulaNivel = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa tu cedula"));
                                    arbol.mostarNivelDato(cedulaNivel);
                                    break;

                                case 6:
                                    int Nivel = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa el nivel"));
                                    arbol.mostrarNivelPersonas(Nivel);
                                    break;

                                case 7:
                                    arbol.ModtrarNodoProfundidad();
                                    break;
                                case 0:
                                    regresarConsultas2 = true;
                                    break;

                                default:
                                    System.out.println("opcion incorrecta");
                            }
                        }
                    }

                    break;

                case 4:
                    //================= MENU OPERACIONES ADICIONALES =================
                    if (arbol.getRaiz() == null) {
                        JOptionPane.showMessageDialog(null, "Arbol vacio");
                    } else {
                        boolean regresarOperaciones = false;

                        while (regresarOperaciones == false) {
                            int opc5 = MenuOtros();

                            switch (opc5) {
                                case 1:
                                    int Nivel = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa el nivel"));
                                    arbol.infoElinminarNivel(Nivel);
                                    break;
                                case 2:
                                    int cedula1 = Integer.parseInt(JOptionPane.showInputDialog(null, "Persona 1: Ingresa tu cedula"));
                                    int cedula2 = Integer.parseInt(JOptionPane.showInputDialog(null, "Persona 2: Ingresa tu cedula"));
                                    arbol.buscarAncestroComun(cedula1, cedula2);
                                    break;
                                case 3:
                                    int cedulaA = Integer.parseInt(JOptionPane.showInputDialog(null, "Persona 1: Ingresa tu cedula"));
                                    int cedulab = Integer.parseInt(JOptionPane.showInputDialog(null, "Persona 2: Ingresa tu cedula"));
                                    arbol.infoAdopcion(cedulaA, cedulab);
                                    break;

                                case 0:
                                    regresarOperaciones = true;
                                    break;

                                default:
                                    System.out.println("opcion incorrecta");
                            }
                        }
                    }
                    break;

                case 0:
                    System.out.println("Salir del programa");
                    System.exit(0);
                    break;

                default:
                    System.out.println("opcion incorrecta");
            }
        } while (true);
    }//Este corchete cierra todo

    //menus para que el usuario escoja la operacion que desea realiazar
    public static int MenuPrincipal() {
        int opc = Integer.parseInt(JOptionPane.showInputDialog("MENU PRINCIPAL"
                + "\n1.	Gestionar personas"
                + "\n2.	Consultar relaciones familiares"
                + "\n3.	Consultar estructurales y visualización"
                + "\n4.	Otras operaciones"
                + "\n0. Salir"
                + "\n\nIngrese una opcion"));
        return opc;
    }//cierre de menu

    public static int MenuPersona() {
        int opc = Integer.parseInt(JOptionPane.showInputDialog("MENU GESTIONAR PERSONAS"
                + "\n1.	Registrar"
                + "\n2.	Eliminar"
                + "\n3.	Actualizar"
                + "\n0. Regresar"
                + "\n\nIngrese una opcion"));
        return opc;
    }//cierre de menu

    public static int MenuRelaciones() {
        int opc = Integer.parseInt(JOptionPane.showInputDialog("MENU CONSULTAR RELACIONES FAMILIARES\n"
                + "\n1.	Padre"
                + "\n2.	Hijos"
                + "\n3.	Hermanos"
                + "\n4.	Tíos"
                + "\n5.	Sobrinos"
                + "\n6.	Primos"
                + "\n7.	Ancestros"
                + "\n8.	Descendientes"
                + "\n0. Regresar"
                + "\n\nIngrese una opcion"));
        return opc;
    }//cierre de menu

    public static int MenuEstructura() {
        int opc = Integer.parseInt(JOptionPane.showInputDialog("MENU CONSULTAS ESTRUCTURALES Y VISUALIZACION\n"
                + "\n1.	Visualizar arbol"
                + "\n2.	Nodo con mayor grado "
                + "\n3.	Familiar mas joven "
                + "\n4.	Altura del arbol"
                + "\n5.	Nivel de un registro"
                + "\n6.	Registros por nivel "
                + "\n7.	Nodo con mayor nivel"
                + "\n0. Regresar"
                + "\n\nIngrese una opcion"));
        return opc;
    }//cierre de menu

    public static int MenuOtros() {
        int opc = Integer.parseInt(JOptionPane.showInputDialog("MENU OTRAS OPERACIONES\n"
                + "\n1.	Eliminar nivel"
                + "\n2.	Ancestro común mas cercano"
                + "\n3.	Trasladar rama (adopción)"
                + "\n0. Regresar"
                + "\n\nIngrese una opcion"));
        return opc;
    }//cierre de menu   
}
