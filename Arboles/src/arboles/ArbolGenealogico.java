package arboles;

import java.time.LocalDate;
import javax.swing.JOptionPane;

public class ArbolGenealogico {

    //atributo 
    public Nodo Raiz;

    //constructor 
    public ArbolGenealogico() {
        this.Raiz = null;
    }

    //getter and setter 
    public Nodo getRaiz() {
        return Raiz;
    }

    public void setRaiz(Nodo Raiz) {
        this.Raiz = Raiz;
    }

    //================= MENU GESTION DE PERSONAS =================
    //metodo construir arbol    
    public void pedirDatos() {

        String Nombre = JOptionPane.showInputDialog("Ingresa tu nombre");
        int Cedula = Integer.parseInt(JOptionPane.showInputDialog("Ingresa tu cedula"));

        //validar que si este bien escrito la fecha de nacimiento 
        LocalDate fechaNacimiento = null;
        boolean valido = false;

        while (valido == false) {
            String fechaTexto = JOptionPane.showInputDialog("Ingresa tu fecha de nacimiento (AAAA-MM-DD):");
            try {
                fechaNacimiento = LocalDate.parse(fechaTexto);
                valido = true;
            } catch (Exception e) {
                JOptionPane.showConfirmDialog(null, "La fecha es incorrecta. Debe ser del tipo de (AAAA-MM-DD)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        //crear objeto de datos
        Persona persona = new Persona(Nombre, Cedula, fechaNacimiento);

        //crear Nodo de datos
        Nodo Nuevo = new Nodo(persona);

        //Pedir datos del padre para llenar 
        if (Raiz == null) {
            Raiz = Nuevo;
            JOptionPane.showMessageDialog(null, "Se ingreso como raiz del arbol a " + Nombre, "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int cedulaPadre = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa la cedula de tu padre"));

            //1.crear objeto que guarda al padre 
            Nodo Padre = buscarNodo(Raiz, cedulaPadre);

            //2.Comprobar si el padre existe
            if (Padre == null) {
                JOptionPane.showMessageDialog(null, "No se encontro la cedula del padre de" + Nombre);
            } else {
                Registrar(Padre, Nuevo); //conectar al padre
                ordenar(Raiz);//ordenar 
                JOptionPane.showMessageDialog(null, "la cedula del padre de" + Nombre, "Cedula encontrada", JOptionPane.INFORMATION_MESSAGE);

            }
        }
    }

    //metodo para buscar entre nodos
    private Nodo buscarNodo(Nodo R, int cedula) {
        Nodo P = R;
        Nodo Resultado = null; //guarda resultados

        while (P != null) {
            if (P.isSw() == false) {
                if (P.getDato().getCedula() == cedula) {
                    Resultado = P;
                }
            } else {
                Nodo encontradoEnHijos = buscarNodo(P.getLigaLista(), cedula); // <-- capturar el resultado
                if (encontradoEnHijos != null) {
                    Resultado = encontradoEnHijos; // <-- guardarlo si se encontró algo
                }
                // también hay que revisar al propio padre, no solo a sus hijos:
                if (P.getDato().getCedula() == cedula) {
                    Resultado = P;
                }
            }
            P = P.getLiga();
        }
        return Resultado;
    }

    public void Registrar(Nodo Padre, Nodo Nuevo) {
        //padre no tiene hijos 
        if (Padre.isSw() == false) {
            Padre.setSw(true);
            Padre.setLigaLista(Nuevo);
        } else {//el padre tiene hijos
            Nodo Q = Padre.getLigaLista();//recorre los hijos del padre
            Nodo Anterior = null;
            boolean Encontrado = false;

            //recorrer
            while (Q != null && Encontrado == true) {
                //comprobar cedula del mayor y poner posicion de Anterior
                if (Q.getDato().getCedula() < Nuevo.getDato().getCedula()) {//comprobar cedula del hijo
                    Anterior = Q;
                    Q = Q.getLiga();
                } else {
                    // encontramos el lugar: la cedula de Q ya es mayor o igual al dato Nuevo
                    Encontrado = false;
                }
            }

            //comprobar si es el primer hijo
            if (Anterior == null) {
                Nuevo.setLiga(Q);
                Padre.setLigaLista(Nuevo);
            } else {//esta en medio
                Anterior.setLiga(Nuevo);
                Nuevo.setLiga(Q);
            }

        }
    }

    //metodo de eliminar 
    private Nodo buscarEdadMayor(Nodo R) {
        Nodo p = R;
        Nodo mayor = null;

        while (p != null) {
            Nodo edadHijo;

            if (p.isSw() == false) {
                edadHijo = p;
            } else {
                edadHijo = p.getLigaLista();
            }

            if (mayor == null || edadHijo.getDato().getFechaNacimiento().isBefore(mayor.getDato().getFechaNacimiento())) {
                mayor = edadHijo;
            }

            p = p.getLiga();
        }

        return mayor;
    }

    public void EliminarInfo(int cedula) {
        if (buscarNodo(Raiz, cedula) == null) {
            JOptionPane.showMessageDialog(null, "La cedula no ha sido encontrada");
        } else {
            Eliminar(Raiz, cedula);
            ordenar(Raiz);
            JOptionPane.showMessageDialog(null, "Persona eliminada correctamente");
        }
    }

    private void Eliminar(Nodo R, int cedula) {
        if (Raiz == null) {
            JOptionPane.showMessageDialog(null, "El arbol esta vacio", "datos no encontrados", JOptionPane.INFORMATION_MESSAGE);
        }
        Nodo p = R;
        Nodo anterior = null;
        while (p != null) {
            if (p.getDato().getCedula() == cedula) {
                if (p.isSw() == false) {//dato no tiene familia (hoja)
                    if (anterior == null) {
                        //era el primero de su lista de hermanos, reconectar con el padre de ARRIBA
                        Nodo padreDeEste = buscarPadre(Raiz, cedula, Raiz);
                        if (padreDeEste == null) {
                            Raiz = p.getLiga();//caso era la raiz misma
                        } else {
                            padreDeEste.setLigaLista(p.getLiga());
                        }
                    } else {
                        anterior.setLiga(p.getLiga());
                    }
                } else {//dato tiene familia 
                    Nodo edadMayor = buscarEdadMayor(p.getLigaLista());
                    Nodo Q = p.getLigaLista();
                    Nodo ant_Q = null;

                    while (Q != edadMayor) {
                        ant_Q = Q;
                        Q = Q.getLiga();
                    }

                    //comprobar si el hijo mayor es padre o no
                    if (edadMayor.isSw() == false) {//hijo mayor no tiene familia
                        p.setDato(edadMayor.getDato());
                        if (Q.getLiga() != null) {
                            ant_Q.setLiga(Q.getLiga());

                        } else if (Q.getLiga() == null) {//hijo unico
                            p.setLigaLista(null);
                            p.setSw(false);

                        } else {
                            ant_Q.setLiga(null);

                        }
                    } else {//hijo mayor tiene familia
                        Nodo x = Q.getLigaLista();
                        Nodo siguiente = Q.getLiga();

                        while (x.getLiga() != null) {//llegar hasta el ultimo hijo
                            x = x.getLiga();
                        }

                        x.setLiga(siguiente);
                        if (ant_Q == null) {
                            p.setLigaLista(Q.getLigaLista());
                        } else {
                            ant_Q.setLiga(Q.getLigaLista());
                        }

                        p.setDato(edadMayor.getDato());
                    }
                }
            } else if (p.isSw() == true) {
                Eliminar(p.getLigaLista(), cedula);
            }
            anterior = p;
            p = p.getLiga();
        }
    }

    public static int pedirDatosActualizar() {
        int opc = Integer.parseInt(JOptionPane.showInputDialog(null, "Selecciona el dato que deseas actualizar"
                + "\n1. Actualizar nombre"
                + "\n2. Actualizar cedula"
                + "\n3. Actualizar fecha de nacimiento"
                + "\n0. Regresar"));
        return opc;
    }

    public void Actualizar(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);

        if (encontrado == null) {
            JOptionPane.showMessageDialog(null, "El dato de la persona ingresada no se encuentra registrada", "dato no encontrado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            boolean actualizar = false;

            while (actualizar == false) {
                int opcion = pedirDatosActualizar();

                switch (opcion) {
                    case 1:
                        String nombreNuevo = JOptionPane.showInputDialog(null, "Ingresa el nuevo nombre");
                        encontrado.getDato().setNombre(nombreNuevo);
                        ordenar(Raiz);
                        break;
                    case 2:
                        int cedulaNuevo = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingresa la nueva cedula"));
                        encontrado.getDato().setCedula(cedulaNuevo);
                        break;
                    case 3:
                        LocalDate fechaNacimiento = null;
                        boolean valido = false;

                        while (valido == false) {
                            String fechaTexto = JOptionPane.showInputDialog("Ingresa tu fecha de nacimiento (AAAA-MM-DD):");
                            try {
                                fechaNacimiento = LocalDate.parse(fechaTexto);
                                valido = true;
                            } catch (Exception e) {
                                JOptionPane.showConfirmDialog(null, "La fecha es incorrecta. Debe ser del tipo de (AAAA-MM-DD)", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }

                        encontrado.getDato().setFechaNacimiento(fechaNacimiento);
                        break;
                    case 0:
                        actualizar = true;
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Opcion incorrecta");
                }
            }
        }
    }

    private void ordenar(Nodo r) {
        if (r != null) {
            Nodo menor = r;
            Nodo p = r.getLiga();

            while (p != null) {
                if (p.getDato().getCedula() < menor.getDato().getCedula()) {
                    menor = p;
                }
                p = p.getLiga();
            }

            Persona temporal = r.getDato();
            r.setDato(menor.getDato());
            menor.setDato(temporal);

            if (r.isSw() == true) {
                ordenar(r.getLigaLista());//ordena también los hijos de r
            }

            ordenar(r.getLiga());
        }
    }

    //================= MENU CONSULTAS FAMILIARES =================
    private Nodo buscarPadre(Nodo R, int cedula, Nodo padre) {
        Nodo encontrado = buscarNodo(Raiz, cedula);

        if (encontrado == null) {
            JOptionPane.showMessageDialog(null, "El dato de la persona ingresada no se encuentra registrada", "dato no encontrado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Nodo p = R;
            Nodo Resultado = null;

            while (p != null && Resultado == null) {
                if (p.getDato().getCedula() == encontrado.getDato().getCedula()) {
                    Resultado = padre;
                } else if (p.isSw() == true) {
                    Resultado = buscarPadre(p.getLigaLista(), cedula, p);
                }
                p = p.getLiga();
            }
            return Resultado;
        }
        return null;
    }

    public void mostrarPadre(int cedula) {
        Nodo padre = buscarPadre(Raiz, cedula, Raiz);

        Nodo hijo = buscarNodo(Raiz, cedula);

        JOptionPane.showMessageDialog(null, "El padre de " + hijo.getDato().getNombre()
                + " es: " + padre.getDato().getNombre());
    }

    public void buscarHijo(Nodo R, int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);

        if (encontrado == null) {
            JOptionPane.showMessageDialog(null, "La cedula no ha sido encontrada");
        } else if (encontrado.isSw() == false) {
            JOptionPane.showMessageDialog(null, "La persona no tiene hijos");
        } else {
            Nodo Q = encontrado.getLigaLista();
            String listaHijos = "";

            while (Q != null) {
                listaHijos = listaHijos + Q.getDato().getNombre() + ", ";
                Q = Q.getLiga();
            }

            JOptionPane.showMessageDialog(null, "Los hijos de " + encontrado.getDato().getNombre() + " son: " + listaHijos);
        }

    }

    public void mostrarHermanos(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);

        if (encontrado == null) {
            JOptionPane.showMessageDialog(null, "La cedula no ha sido encontrada");
        } else {
            Nodo padre = buscarPadre(Raiz, encontrado.getDato().getCedula(), Raiz);
            if (Raiz.getDato() == encontrado.getDato()) {
                JOptionPane.showMessageDialog(null, encontrado.getDato().getNombre() + " no tiene hermanos, es la raiz");
            } else {

                Nodo Q = padre.getLigaLista();
                String listaHijos = "";

                while (Q != null) {
                    if (Q.getDato().getCedula() != encontrado.getDato().getCedula()) {
                        listaHijos = listaHijos + Q.getDato().getNombre() + ", ";
                    }
                    Q = Q.getLiga();
                }
                if (listaHijos.equals("")) {
                    JOptionPane.showMessageDialog(null, "No tiene hermanos");
                } else {
                    JOptionPane.showMessageDialog(null, "Los hermanos de " + encontrado.getDato().getNombre() + " son: " + listaHijos);
                }
            }
        }
    }

    public void mostrarTios(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);
        if (encontrado == null) {
            JOptionPane.showMessageDialog(null, "La cedula no ha sido encontrada");
        } else if (encontrado.getDato() == Raiz.getDato()) {
            JOptionPane.showMessageDialog(null, "Es la raiz, no tiene tios");
        } else {
            Nodo padre = buscarPadre(Raiz, encontrado.getDato().getCedula(), Raiz);
            if (Raiz.getDato() == padre.getDato()) {
                JOptionPane.showMessageDialog(null, "El padre de " + encontrado.getDato().getNombre() + " es la raiz, no tiene tios");
            } else {
                Nodo Abuelo = buscarPadre(Raiz, padre.getDato().getCedula(), Raiz);

                Nodo buscar = Abuelo.getLigaLista();
                String listaHijos = "";

                while (buscar != null) {
                    if (buscar.getDato().getCedula() != padre.getDato().getCedula()) {
                        listaHijos = listaHijos + buscar.getDato().getNombre() + ", ";
                    }
                    buscar = buscar.getLiga();
                }
                if (listaHijos.equals("")) {
                    JOptionPane.showMessageDialog(null, "No tiene tios");
                } else {
                    JOptionPane.showMessageDialog(null, "Los tios de " + encontrado.getDato().getNombre() + " son: " + listaHijos);
                }
            }
        }
    }

    public void mostrarSobrinos(int cedula) {
        Nodo encontrar = buscarNodo(Raiz, cedula);

        if (encontrar == null) {
            JOptionPane.showMessageDialog(null, "La cedula no ha sido encontrada");
        } else {
            Nodo padre = buscarPadre(Raiz, cedula, Raiz);
            if (encontrar.getDato() == Raiz.getDato()) {
                JOptionPane.showMessageDialog(null, encontrar.getDato().getNombre() + " no tiene hermanos, es la raiz");
            } else {
                Nodo buscar = padre.getLigaLista();
                String listaSobrinos = "";

                while (buscar != null) {
                    if (buscar.getDato() != encontrar.getDato() && buscar.isSw() == true) {
                        Nodo sobrinos = buscar.getLigaLista();
                        while (sobrinos != null) {
                            listaSobrinos = listaSobrinos + sobrinos.getDato().getNombre() + ", ";
                            sobrinos = sobrinos.getLiga();
                        }
                    }
                    buscar = buscar.getLiga();
                }

                if (listaSobrinos.equals("")) {
                    JOptionPane.showMessageDialog(null, "No tiene sobrinos");
                } else {
                    JOptionPane.showMessageDialog(null, "Los sobrinos de " + encontrar.getDato().getNombre() + " son: " + listaSobrinos);
                }
            }
        }
    }

    public void mostrarPrimos(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);
        if (encontrado == null) {//comprobar si la cedula no existe
            JOptionPane.showMessageDialog(null, "La cedula no ha sido encontrada");
        } else {
            Nodo padre = buscarPadre(Raiz, encontrado.getDato().getCedula(), Raiz);//llamar el metodo para buscar el padre
            if (Raiz.getDato() == padre.getDato()) {
                JOptionPane.showMessageDialog(null, "El padre de " + encontrado.getDato().getNombre() + " es la raiz");
            } else {
                Nodo Abuelo = buscarPadre(Raiz, padre.getDato().getCedula(), Raiz);
                Nodo buscar = Abuelo.getLigaLista();
                String listaPrimos = "";

                while (buscar != null) {
                    if (buscar.getDato().getCedula() != padre.getDato().getCedula() && buscar.isSw() == true) {//salto a mi padre

                        Nodo Q = buscar.getLigaLista();

                        while (Q != null) {
                            listaPrimos = listaPrimos + Q.getDato().getNombre() + ", ";
                            Q = Q.getLiga();
                        }
                    }
                    buscar = buscar.getLiga();
                }
                if (listaPrimos.equals("")) {
                    JOptionPane.showMessageDialog(null, "No tiene primos");
                } else {
                    JOptionPane.showMessageDialog(null, "Los primos de " + encontrado.getDato().getNombre() + " son: " + listaPrimos);
                }
            }
        }
    }

    private String busquedaAncestros(Nodo p, int cedula) {
        Nodo padre = buscarPadre(Raiz, cedula, null);
        String listaAncestros = "";
        while (padre.getDato() != Raiz.getDato()) {
            listaAncestros = listaAncestros + padre.getDato().getNombre() + ", ";
            padre = buscarPadre(Raiz, padre.getDato().getCedula(), null);
        }
        listaAncestros = listaAncestros + Raiz.getDato().getNombre();//se agrega al final, fuera del while

        return listaAncestros;
    }

    public void mostrarAncestros(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);

        if (encontrado == null) {
            JOptionPane.showMessageDialog(null, "La cedula no ha sido encontrada");
        } else if (encontrado.getDato() == Raiz.getDato()) {
            JOptionPane.showMessageDialog(null, "No tiene ancestros, es la raiz");
        } else {
            String persona = busquedaAncestros(encontrado, encontrado.getDato().getCedula());
            if (persona == "") {
                JOptionPane.showMessageDialog(null, "No tiene ancestros");
            } else {
                JOptionPane.showMessageDialog(null, "Los ancestros de " + encontrado.getDato().getNombre() + " son: " + persona);
            }
        }
    }

    public void mostrarDescendientes(int cedula) {
        Nodo persona = buscarNodo(Raiz, cedula);

        if (persona == null) {
            JOptionPane.showMessageDialog(null, "La cédula no ha sido encontrada");
            return;
        }

        if (persona.isSw() == false) {
            JOptionPane.showMessageDialog(null, "Esta persona no tiene descendientes");
            return;
        }

        String descendientes = buscarDescendientesDesde(persona.getLigaLista());

        JOptionPane.showMessageDialog(null,
                "Los descendientes de " + persona.getDato().getNombre() + " son: " + descendientes);
    }

    private String buscarDescendientesDesde(Nodo p) {
        String resultado = "";
        while (p != null) {
            resultado = resultado + p.getDato().getNombre() + ", ";
            if (p.isSw() == true) {
                resultado = resultado + buscarDescendientesDesde(p.getLigaLista());   // baja a SUS hijos, no busca de nuevo
            }
            p = p.getLiga();   // sigue con el hermano siguiente, sin cortar el ciclo
        }
        return resultado;
    }

//================= MENU CONSULTAS ESTRUCTURALES =================
    private Nodo mayorGrado(Nodo R, Nodo padre, int cont) {
        Nodo p = R;
        int contguardar = 0;
        Nodo Resultado = null;
        while (p != null) {
            cont++;
            if (p.isSw() == true) {
                mayorGrado(p.getLigaLista(), p, cont + 1);
            }
            p = p.getLiga();
        }

        if (padre == null || cont > contguardar) {
            contguardar = cont;
            Resultado = padre;
        }
        return Resultado;
    }

    public void mostrarGrado() {
        Nodo grado = mayorGrado(Raiz, Raiz, 0);

        if (grado == null) {
            JOptionPane.showMessageDialog(null, "Grado no identidicado");
        } else {
            JOptionPane.showMessageDialog(null, "El nodo con mayor grado es " + grado.getDato().getNombre());
        }
    }

    private Nodo buscarEdadMenor(Nodo r) {
        Nodo mejorNodo = null;
        Nodo p = r;

        while (p != null) {
            Nodo nodoConDato;

            if (p.isSw() == false) {
                nodoConDato = p;
            } else {
                nodoConDato = buscarEdadMenor(p.getLigaLista());
            }

            if (nodoConDato != null) {
                if (mejorNodo == null || nodoConDato.getDato().getFechaNacimiento().isAfter(mejorNodo.getDato().getFechaNacimiento())) {
                    mejorNodo = nodoConDato;
                }
            }

            p = p.getLiga();
        }

        return mejorNodo;
    }

    public void mostrarMenor() {
        Nodo edadMenor = buscarEdadMenor(Raiz);

        if (edadMenor == null) {
            JOptionPane.showMessageDialog(null, "Hijo menor no encontrado");
        } else {
            JOptionPane.showMessageDialog(null, "El hijo menor es " + edadMenor.getDato().getNombre());
        }
    }

    private int buscarNivel(Nodo r, int mayorProfundidad) {
        Nodo p = r;

        while (p != null) {
            int profundidad;

            if (p.isSw() == false) {
                profundidad = 1;//hoja: cuenta como 1 nivel
            } else {
                profundidad = buscarNivel(p.getLigaLista(), mayorProfundidad + 1);//el +1 es por este mismo nivel, sumado a lo que devuelva la rama
            }

            if (profundidad > mayorProfundidad) {
                mayorProfundidad = profundidad;
            }

            p = p.getLiga();
        }

        return mayorProfundidad;
    }

    public void mostarNivel() {
        int nivel = buscarNivel(Raiz, 0);

        if (nivel == 0) {
            JOptionPane.showMessageDialog(null, "No hay niveles");
        } else {
            JOptionPane.showMessageDialog(null, "El nivel total del arbol es " + nivel);
        }
    }

    private int buscarNivelDato(Nodo R, int cedula, int nivel) {
        Nodo p = R;
        int mayorProfundidad = -1;

        while (p != null && mayorProfundidad == -1) {
            if (p.getDato().getCedula() == cedula) {
                mayorProfundidad = nivel;
            } else if (p.isSw() == true) {
                mayorProfundidad = buscarNivelDato(p.getLigaLista(), cedula, nivel + 1);
            }
            p = p.getLiga();
        }
        return mayorProfundidad;
    }

    public void mostarNivelDato(int cedula) {
        Nodo persona = buscarNodo(Raiz, cedula);

        if (persona == null) {
            JOptionPane.showMessageDialog(null, "La cedula no existe");
        } else {
            int nivel = buscarNivelDato(Raiz, cedula, 0);
            JOptionPane.showMessageDialog(null, "El nivel de " + persona.getDato().getNombre() + " es " + nivel);
        }
    }

    private String nivelPersonas(Nodo R, int nivel, int cont) {
        String listaNivel = "";
        Nodo p = R;

        while (p != null) {
            if (cont == nivel) {
                listaNivel = listaNivel + p.getDato().getNombre() + ", ";
            } else if (p.isSw() == true) {
                listaNivel = listaNivel + nivelPersonas(p.getLigaLista(), nivel, cont + 1);
            }
            p = p.getLiga();
        }
        return listaNivel;
    }

    public void mostrarNivelPersonas(int nivel) {
        String personas = nivelPersonas(Raiz, nivel, 0);

        if (personas == "") {
            JOptionPane.showMessageDialog(null, "No hay personas en ese nivel");
        } else {
            JOptionPane.showMessageDialog(null, "Las personas de ese nivel son " + personas);
        }
    }

    private Nodo buscarNodoProfundidad(Nodo R) {
        Nodo p = R;
        int nivelMejor = -1;
        Nodo resultado = null;

        while (p != null) {
            //si es una hoja 
            int nivelActual = buscarNivelDato(Raiz, p.getDato().getCedula(), 1);//buscar en que nivel estamos ahora
            if (nivelActual > nivelMejor) {//comparar para saber si guardar dato o no
                nivelMejor = nivelActual;
                resultado = p;
            }

            if (p.isSw() == true) {//si tiene hijos se debe de buscar entre ellos si tienen mas familia tambien 
                Nodo candidato = buscarNodoProfundidad(p.getLigaLista());
                if (candidato != null) {
                    int nivelCandidato = buscarNivelDato(Raiz, candidato.getDato().getCedula(), 1);//trambien saber en que posicion de nivel estamos 
                    if (nivelCandidato > nivelMejor) {
                        nivelMejor = nivelCandidato;
                        resultado = candidato;
                    }
                }
            }
            p = p.getLiga();
        }
        return resultado;
    }

    public void ModtrarNodoProfundidad() {
        Nodo persona = buscarNodoProfundidad(Raiz);

        if (persona == null) {
            JOptionPane.showMessageDialog(null, "Arbol vacio");

        } else {
            int nivel = buscarNivelDato(Raiz, persona.getDato().getCedula(), 1);
            JOptionPane.showMessageDialog(null, "La informacion de esta persona es: "
                    + "\n Nombre: " + persona.getDato().getNombre()
                    + "\n Cedula: " + persona.getDato().getCedula()
                    + "\n Fecha de nacimiento: " + persona.getDato().getFechaNacimiento()
                    + "\n Nivel: " + nivel);
        }
    }

    //================= MENU OPERACIONES ADICIONALES =================
    //pasa de nombres a cedulas
    private String nivelPersonasCedulas(Nodo R, int nivel, int cont) {

        Nodo p = R;
        String lista = "";

        while (p != null) {
            if (cont == nivel) {
                lista = lista + p.getDato().getCedula() + ", ";
            } else if (p.isSw() == true) {
                lista = lista + nivelPersonasCedulas(p.getLigaLista(), nivel, cont + 1);
            }
            p = p.getLiga();
        }
        return lista;
    }

    public void infoElinminarNivel(int nivel) {
        String personas = nivelPersonasCedulas(Raiz, nivel, 0);

        if (personas.equals("")) {
            JOptionPane.showMessageDialog(null, "En este nivel no existen datos");
        } else {
            String[] partes = personas.split(", ");

            for (int i = 0; i < partes.length; i++) {
                //el nombre viene guardado en "personas", no la cedula
                //ver nota abajo sobre este punto
                int cedula = Integer.parseInt(partes[i]);
                Eliminar(Raiz, cedula);
            }

            JOptionPane.showMessageDialog(null, "Nivel eliminado con exito");
        }
    }

    public void buscarAncestroComun(int cedula1, int cedula2) {
        Nodo persona1 = buscarNodo(Raiz, cedula1);
        Nodo persona2 = buscarNodo(Raiz, cedula2);

        if (persona1 == null) {
            JOptionPane.showMessageDialog(null, "Persona uno no encontrada");
        } else if (persona2 == null) {
            JOptionPane.showMessageDialog(null, "Persona dos no encontrada");
        } else {
            Nodo actual1 = persona1;
            Nodo actual2 = persona2;
            Nodo comun = null;

            while (comun == null) {//si no encuentra dato
                if (actual1.getDato().getCedula() == actual2.getDato().getCedula()) {
                    comun = actual1;
                } else {
                    if (actual1.getDato().getCedula() != Raiz.getDato().getCedula()) {
                        actual1 = buscarPadre(Raiz, actual1.getDato().getCedula(), Raiz);
                    }
                    if (actual2.getDato().getCedula() != Raiz.getDato().getCedula()) {
                        actual2 = buscarPadre(Raiz, actual2.getDato().getCedula(), Raiz);
                    }
                }
            }

            JOptionPane.showMessageDialog(null, "El ancestro comun mas cercano es: " + comun.getDato().getNombre());
        }
    }

    public void infoAdopcion(int cedulaA, int cedulaB) {
        if (buscarNodo(Raiz, cedulaA) == null) {
            JOptionPane.showMessageDialog(null, "Persona uno no encontrada");
        } else if (buscarNodo(Raiz, cedulaB) == null) {
            JOptionPane.showMessageDialog(null, "Persona dos no encontrada");
        } else if (cedulaA == cedulaB) {
            JOptionPane.showMessageDialog(null, "Ingreso la misma cedula para ambos");
        } else {
            Nodo padreDeB = buscarPadre(Raiz, cedulaB, Raiz);

            if (padreDeB != null && padreDeB.getDato().getCedula() == cedulaA) {
                JOptionPane.showMessageDialog(null, "B ya es hijo de A");
            } else {
                Nodo personaA = buscarCedulaA(Raiz, cedulaA);
                buscarCedulaB(Raiz, cedulaB, personaA);
                JOptionPane.showMessageDialog(null, "Traslado realizado con exito");
            }
        }

    }

    private Nodo buscarCedulaA(Nodo R, int cedulaA) {
        Nodo p = R;
        Nodo anterior = null;
        Nodo guardadoA = null;

        while (p != null && guardadoA == null) {
            if (p.getDato().getCedula() == cedulaA) {
                //comprobar como esta cedula A
                guardadoA = p;
                if (anterior == null) {//hoja
                    Nodo padre = buscarPadre(Raiz, cedulaA, Raiz);
                    if (padre == null) {
                        Raiz = p.getLiga();
                    } else {
                        padre.setLigaLista(p.getLiga());
                        if (p.getLiga() == null) {
                            padre.setSw(false);
                        }
                    }
                } else {
                    anterior.setLiga(p.getLiga());
                }
                p.setLiga(null);
            } else if (p.isSw() == true) {
                guardadoA = buscarCedulaA(p.getLigaLista(), cedulaA);
            }
            anterior = p;
            p = p.getLiga();
        }
        return guardadoA;
    }

    private boolean buscarCedulaB(Nodo R, int cedulaB, Nodo personaA) {
        Nodo p = R;
        boolean encontrado = false;

        while (p != null && encontrado == false) {
            if (p.getDato().getCedula() == cedulaB) {
                Registrar(p, personaA);
                encontrado = true;

            } else if (p.isSw() == true) {
                encontrado = buscarCedulaB(p.getLigaLista(), cedulaB, personaA);
            }
            p = p.getLiga();
        }
        return encontrado;
    }
}//final metodo

