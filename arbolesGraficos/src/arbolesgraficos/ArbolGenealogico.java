package arbolesgraficos;

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

    public void cargarArbolPrueba() {
        Nodo x = new Nodo(new Persona("X", 100, LocalDate.of(1950, 1, 10)));
        Nodo c = new Nodo(new Persona("C", 150, LocalDate.of(1975, 3, 22)));
        Nodo f = new Nodo(new Persona("F", 200, LocalDate.of(1977, 7, 5)));
        Nodo h = new Nodo(new Persona("H", 250, LocalDate.of(1979, 11, 30)));
        Nodo d = new Nodo(new Persona("D", 400, LocalDate.of(1998, 2, 14)));
        Nodo e = new Nodo(new Persona("E", 450, LocalDate.of(2000, 6, 9)));
        Nodo w = new Nodo(new Persona("W", 500, LocalDate.of(1999, 4, 18)));
        Nodo g = new Nodo(new Persona("G", 550, LocalDate.of(2001, 9, 27)));
        Nodo k = new Nodo(new Persona("K", 600, LocalDate.of(2002, 12, 3)));
        Nodo a = new Nodo(new Persona("A", 700, LocalDate.of(2020, 5, 15)));
        Nodo b = new Nodo(new Persona("B", 750, LocalDate.of(2021, 8, 21)));
        Nodo z = new Nodo(new Persona("Z", 800, LocalDate.of(2019, 10, 2)));

        //enlazar hermanos entre si (getLiga)
        c.setLiga(f);
        f.setLiga(h);

        d.setLiga(e);

        w.setLiga(g);
        g.setLiga(k);

        a.setLiga(b);

        //marcar quien tiene hijos (sw=true) y apuntar a su lista de hijos (getLigaLista)
        x.setSw(true);
        x.setLigaLista(c);

        c.setSw(true);
        c.setLigaLista(d);
        //F no tiene hijos, queda como hoja

        h.setSw(true);
        h.setLigaLista(w);

        w.setSw(true);
        w.setLigaLista(a);

        g.setSw(true);
        g.setLigaLista(z);
        //K no tiene hijos, queda como hoja

        Raiz = x;
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
                Nodo encontradoEnHijos = buscarNodo(P.getLigaLista(), cedula); //capturar el resultado
                if (encontradoEnHijos != null) {
                    Resultado = encontradoEnHijos; //guardarlo si se encontró algo
                }
                // también hay que revisar al propio padre, no solo a sus hijos
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
            Nodo edadHijo = p;

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
                        if (ant_Q == null) {
                            //aca SI es el primero de la lista: puede ser hijo unico, o el primero con hermanos despues
                            p.setLigaLista(Q.getLiga());
                            if (Q.getLiga() == null) {
                                p.setSw(false);
                            }
                        } else {
                            //habia alguien antes de Q: nunca se toca la LigaLista de p
                            ant_Q.setLiga(Q.getLiga());
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

    // profundidad (numero de niveles) del subarbol que cuelga de r
    private int buscarNivel(Nodo r) {
        int resultado;

        if (r.isSw() == false) {
            resultado = 0; //hoja: no tiene nada debajo, es el nivel 0 de su propia rama
        } else {
            int maxProfundidadHijos = 0;
            Nodo p = r.getLigaLista();

            while (p != null) {
                int profundidadHijo = buscarNivel(p);
                if (profundidadHijo > maxProfundidadHijos) {
                    maxProfundidadHijos = profundidadHijo;
                }
                p = p.getLiga();
            }

            resultado = 1 + maxProfundidadHijos;
        }

        return resultado;
    }

    public void mostarNivel() {
        if (Raiz == null) {
            JOptionPane.showMessageDialog(null, "El arbol esta vacio");
        } else {
            int nivel = buscarNivel(Raiz);
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
        if (Raiz == null) {
            JOptionPane.showMessageDialog(null, "Arbol vacio");
            return;
        }

        int nivelMasProfundo = buscarNivel(Raiz); // ya viene en base 0, sin restar nada

        String personas = nivelPersonas(Raiz, nivelMasProfundo, 0);
        JOptionPane.showMessageDialog(null, "Las personas en el nivel mas profundo (nivel " + nivelMasProfundo + ") son:" + personas);
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
        if (Raiz == null) {
            JOptionPane.showMessageDialog(null, "El arbol esta vacio");
            return;
        }
        if (nivel == 0) {
            JOptionPane.showMessageDialog(null, "No se puede eliminar el nivel de la raiz");
            return;
        }

        String personas = nivelPersonasCedulas(Raiz, nivel, 0); //el mismo metodo que ya tenias

        if (personas.equals("")) {
            JOptionPane.showMessageDialog(null, "En este nivel no existen datos");
        } else {
            String[] partes = personas.split(", ");

            for (int i = 0; i < partes.length; i++) {
                int cedula = Integer.parseInt(partes[i]);
                eliminarUnoDelNivel(cedula); //reemplaza a Eliminar()
            }

            JOptionPane.showMessageDialog(null, "Nivel eliminado con exito");
        }
    }

    private void eliminarUnoDelNivel(int cedula) {
        Nodo nodo = buscarNodo(Raiz, cedula);
        if (nodo == null) {
            return;
        }

        Nodo padre = buscarPadre(Raiz, cedula, Raiz);
        Nodo listaHermanos = (padre == null) ? Raiz : padre.getLigaLista();

        Nodo actual = listaHermanos;
        Nodo anterior = null;
        while (actual != nodo) {
            anterior = actual;
            actual = actual.getLiga();
        }

        Nodo reemplazo; //lo que ocupa el lugar del nodo eliminado
        if (nodo.isSw() == false) {
            reemplazo = nodo.getLiga(); //no tenia hijos: se salta
        } else {
            Nodo hijos = nodo.getLigaLista();
            Nodo ultimoHijo = hijos;
            while (ultimoHijo.getLiga() != null) {
                ultimoHijo = ultimoHijo.getLiga();
            }
            ultimoHijo.setLiga(nodo.getLiga()); //los hijos quedan seguidos de sus antiguos "tios"
            reemplazo = hijos;
        }

        if (anterior == null) {
            if (padre == null) {
                Raiz = reemplazo;
            } else {
                padre.setLigaLista(reemplazo);
                padre.setSw(reemplazo != null);
            }
        } else {
            anterior.setLiga(reemplazo);
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

    //==========================================IMPLEMENTACION PARA ARBOL EN JFRAME(SWING)==================================================================
    //1. Gestión de Personas
    public String registrarVentana(String Nombre, int Cedula, String fechaTexto, int cedulaPadre) {
        //validar que si este bien escrito la fecha de nacimiento 
        LocalDate fechaNacimiento = null;
        try {
            fechaNacimiento = LocalDate.parse(fechaTexto);

        } catch (Exception e) {
            return "La fecha es incorrecta. Debe ser del tipo de (AAAA-MM-DD)";
        }

        //crear objeto de datos
        Persona persona = new Persona(Nombre, Cedula, fechaNacimiento);

        //crear Nodo de datos
        Nodo Nuevo = new Nodo(persona);
        String resultado;

        //Pedir datos del padre para llenar 
        if (Raiz == null) {
            Raiz = Nuevo;
            resultado = "Se ingreso como raiz del arbol a " + Nombre;
        } else {
            resultado = "Ingresa la cedula de tu padre";

            //1.crear objeto que guarda al padre 
            Nodo Padre = buscarNodo(Raiz, cedulaPadre);

            //2.Comprobar si el padre existe
            if (Padre == null) {
                resultado = "No se encontro la cedula del padre de" + Nombre;
            } else {
                Registrar(Padre, Nuevo); //conectar al padre
                ordenar(Raiz);//ordenar 
                resultado = "Se registro a " + Nombre + " como hijo de la cedula " + cedulaPadre;

            }
        }
        return resultado;
    }

    public Persona buscarPersonaVentana(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);
        if (encontrado == null) {
            return null;
        }
        return encontrado.getDato();
    }

    public String eliminarVentana(int cedula) {

        if (buscarNodo(Raiz, cedula) == null) {
            return "La cedula no ha sido encontrada";
        } else {
            Eliminar(Raiz, cedula);
            ordenar(Raiz);
            return "Persona eliminada correctamente";
        }
    }

    private Nodo actualizarVentana(int cedula) {
        return buscarNodo(Raiz, cedula);
    }

    public String actualizarVentana(int cedula, String nombreNuevo, int cedulaNueva, String fechaTexto) {
        Nodo encontrado = buscarNodo(Raiz, cedula);

        if (encontrado == null) {
            return "El dato de la persona ingresada no se encuentra registrada";
        }
        try {
            encontrado.getDato().setNombre(nombreNuevo);
            encontrado.getDato().setCedula(cedulaNueva);
            LocalDate fechaNacimiento = LocalDate.parse(fechaTexto);
            encontrado.getDato().setFechaNacimiento(fechaNacimiento);
            ordenar(Raiz);
            return "Datos actualizados";
        } catch (Exception e) {
            return "La fecha es incorrecta. Debe ser del tipo de (AAAA-MM-DD)";
        }
    }

    //2. 
    public String padreVentana(int cedula) {
        Nodo hijo = buscarNodo(Raiz, cedula);
        if (hijo == null) {
            return "Cedula no encontrada";
        }

        Nodo padre = buscarPadre(Raiz, cedula, Raiz);
        if (padre == null) {
            return hijo.getDato().getNombre() + " es la raiz, no tiene padre";
        }

        return "El padre de " + hijo.getDato().getNombre() + " es: " + padre.getDato().getNombre();
    }

    public String hijoVentana(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);

        if (encontrado == null) {
            return "La cedula no ha sido encontrada";
        }
        if (encontrado.isSw() == false) {
            return "La persona no tiene hijos";
        }

        Nodo Q = encontrado.getLigaLista();
        String listaHijos = "";

        while (Q != null) {
            listaHijos = listaHijos + Q.getDato().getNombre() + ", ";
            Q = Q.getLiga();
        }
        return "Los hijos de " + encontrado.getDato().getNombre() + " son: " + listaHijos;

    }

    public String hermanoVentana(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);

        if (encontrado == null) {
            return "La cedula no ha sido encontrada";
        }

        Nodo padre = buscarPadre(Raiz, encontrado.getDato().getCedula(), Raiz);
        if (Raiz.getDato() == encontrado.getDato()) {
            return encontrado.getDato().getNombre() + " no tiene hermanos, es la raiz";
        }
        Nodo Q = padre.getLigaLista();
        String listaHijos = "";

        while (Q != null) {
            if (Q.getDato().getCedula() != encontrado.getDato().getCedula()) {
                listaHijos = listaHijos + Q.getDato().getNombre() + ", ";
            }
            Q = Q.getLiga();
        }
        if (listaHijos.equals("")) {
            return "No tiene hermanos";
        }
        return "Los hermanos de " + encontrado.getDato().getNombre() + " son: " + listaHijos;
    }

    public String tioVentana(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);
        if (encontrado == null) {
            return "La cedula no ha sido encontrada";
        }
        if (encontrado.getDato() == Raiz.getDato()) {
            return "Es la raiz, no tiene tios";
        }

        Nodo padre = buscarPadre(Raiz, encontrado.getDato().getCedula(), Raiz);
        if (Raiz.getDato() == padre.getDato()) {
            return "El padre de " + encontrado.getDato().getNombre() + " es la raiz, no tiene tios";
        }
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
            return "No tiene tios";
        }
        return "Los tios de " + encontrado.getDato().getNombre() + " son: " + listaHijos;
    }

    public String sobrinosVentana(int cedula) {
        Nodo encontrar = buscarNodo(Raiz, cedula);

        if (encontrar == null) {
            return "La cedula no ha sido encontrada";
        }

        Nodo padre = buscarPadre(Raiz, cedula, Raiz);
        if (encontrar.getDato() == Raiz.getDato()) {
            return encontrar.getDato().getNombre() + " no tiene hermanos, es la raiz";
        }

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
            return "No tiene sobrinos";
        }
        return "Los sobrinos de " + encontrar.getDato().getNombre() + " son: " + listaSobrinos;
    }

    public String primosVentana(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);
        if (encontrado == null) {//comprobar si la cedula no existe
            return "La cedula no ha sido encontrada";
        }
        Nodo padre = buscarPadre(Raiz, encontrado.getDato().getCedula(), Raiz);//llamar el metodo para buscar el padre
        if (Raiz.getDato() == padre.getDato()) {
            return "El padre de " + encontrado.getDato().getNombre() + " es la raiz";
        }
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
            return "No tiene primos";
        }
        return "Los primos de " + encontrado.getDato().getNombre() + " son: " + listaPrimos;
    }

    public String ancestrosVentana(int cedula) {
        Nodo encontrado = buscarNodo(Raiz, cedula);
        if (encontrado == null) {
            return "La cedula no ha sido encontrada";
        }
        if (encontrado.getDato() == Raiz.getDato()) {
            return "No tiene ancestros, es la raiz";
        }

        String persona = busquedaAncestros(encontrado, encontrado.getDato().getCedula());
        if (persona == "") {
            return "No tiene ancestros";
        }
        return "Los ancestros de " + encontrado.getDato().getNombre() + " son: " + persona;
    }

    public String descendientesVentana(int cedula) {
        Nodo persona = buscarNodo(Raiz, cedula);
        if (persona == null) {
            return "La cédula no ha sido encontrada.";
        }
        if (persona.isSw() == false) {
            return "Esta persona no tiene descendientes.";
        }
        String descendientes = buscarDescendientesDesde(persona.getLigaLista());
        return "Los descendientes de " + persona.getDato().getNombre() + " son: " + descendientes;
    }

    //3.Consultas Estructurales y Visualización
    public String gradoVentana() {
        Nodo grado = mayorGrado(Raiz, Raiz, 0);

        if (grado == null) {
            return "Grado no identidicado";
        }
        return "El nodo con mayor grado es " + grado.getDato().getNombre();
    }

    public String menorEdadVentana() {
        Nodo edadMenor = buscarEdadMenor(Raiz);

        if (edadMenor == null) {
            return "Hijo menor no encontrado";
        }
        return "El hijo menor es " + edadMenor.getDato().getNombre();
    }

    public String nivelventana() {
        int nivel = buscarNivel(Raiz);

        if (nivel == 0) {
            return "No hay niveles";
        }
        return "El nivel total del arbol es " + nivel;
    }

    public String nivelDatoVentana(int cedula) {
        Nodo persona = buscarNodo(Raiz, cedula);

        if (persona == null) {
            return "La cedula no existe";
        }
        int nivel = buscarNivelDato(Raiz, cedula, 0);
        return "El nivel de " + persona.getDato().getNombre() + " es " + nivel;

    }

    public String nivelPersonasVentana(int nivel) {
        String personas = nivelPersonas(Raiz, nivel, 0);

        if (personas == "") {
            return "No hay personas en ese nivel";
        }
        return "Las personas de ese nivel son " + personas;
    }

    public String nodoProfundidadVentana() {
        Nodo persona = buscarNodoProfundidad(Raiz);

        if (persona == null) {
            return "Arbol vacio";
        }
        int nivel = buscarNivelDato(Raiz, persona.getDato().getCedula(), 1);
        return "La informacion de esta persona es: "
                + "\n Nombre: " + persona.getDato().getNombre()
                + "\n Cedula: " + persona.getDato().getCedula()
                + "\n Fecha de nacimiento: " + persona.getDato().getFechaNacimiento()
                + "\n Nivel: " + nivel;
    }

    //4. Otros
    public String eliminarNivelVentana(int nivel) {
        String personas = nivelPersonasCedulas(Raiz, nivel, 0);

        if (personas.equals("")) {
            return "En este nivel no existen datos";
        }
        String[] partes = personas.split(", ");

        for (int i = 0; i < partes.length; i++) {
            int cedula = Integer.parseInt(partes[i]);
            Eliminar(Raiz, cedula);
        }

        return "Nivel eliminado con exito";
    }

    public String ancestroComunVentana(int cedula1, int cedula2) {
        Nodo persona1 = buscarNodo(Raiz, cedula1);
        Nodo persona2 = buscarNodo(Raiz, cedula2);

        if (persona1 == null) {
            return "Persona uno no encontrada";
        }
        if (persona2 == null) {
            return "Persona dos no encontrada";
        }
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
        return "El ancestro comun mas cercano es: " + comun.getDato().getNombre();
    }

    public String adopcionVentana(int cedulaA, int cedulaB) {
        if (buscarNodo(Raiz, cedulaA) == null) {
            return "Persona uno no encontrada";
        }
        if (buscarNodo(Raiz, cedulaB) == null) {
            return "Persona dos no encontrada";
        }
        if (cedulaA == cedulaB) {
            return "Ingreso la misma cedula para ambos";
        }
        Nodo padreDeB = buscarPadre(Raiz, cedulaB, Raiz);

        if (padreDeB != null && padreDeB.getDato().getCedula() == cedulaA) {
            return "B ya es hijo de A";
        }
        Nodo personaA = buscarCedulaA(Raiz, cedulaA);
        buscarCedulaB(Raiz, cedulaB, personaA);
        return "Traslado realizado con exito";
    }

}//final metodo

