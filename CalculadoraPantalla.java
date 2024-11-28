import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.regex.*;
import javax.swing.*;

public class CalculadoraPantalla extends JFrame {

    // Creamos una variable global para el JTextArea
    private JTextArea areaTexto;
    private JTextArea areaAlmacenado; // Sección para mostrar el dato almacenado
    private boolean tecladoOn = true;

    public CalculadoraPantalla() {
        super("Calculadora de Víctor"); // nombre de la pestaña

        // Panel principal con diseño nulo para controlar manualmente el tamaño
        JPanel pantallaSecundaria = new JPanel(); // crea la pantalla donde se escribe texto
        pantallaSecundaria.setBackground(Color.LIGHT_GRAY); // color de pantalla
        pantallaSecundaria.setLayout(new BorderLayout()); // El borde de layout
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); // Recoge las dimensiones de la pantalla
        pantallaSecundaria.setBounds(10, 10, d.width / 2, 100); // posición y tamaño fijo de la pantalla

        areaTexto = new JTextArea(); // permite ingresar texto
        areaTexto.setLineWrap(true); // controla la longitud del texto cambiando a una segunda línea en caso de ser necesario
        areaTexto.setWrapStyleWord(true); // controla que el texto no sea cortado a la mitad
        areaTexto.setFont(new Font("Arial", Font.PLAIN, 14)); // La fuente en el area de texto
        areaTexto.setEditable(false); // Evita que el usuario escriba directamente en el área de texto, sin esto acepta todo tipo de escritura

        JScrollPane scrollTexto = new JScrollPane(areaTexto); // ayuda a corregir el cambio de lineas al escribir en la

        if(tecladoOn){ // Si el teclado está activado
            activarTeclado();   // Permite la escritura de teclado
        }

        // Crear y configurar el área para mostrar el dato almacenado
        areaAlmacenado = new JTextArea();
        areaAlmacenado.setLineWrap(true);
        areaAlmacenado.setWrapStyleWord(true);
        areaAlmacenado.setFont(new Font("Arial", Font.PLAIN, 14));
        areaAlmacenado.setEditable(false);
        JScrollPane scrollAlmacenado = new JScrollPane(areaAlmacenado);

        // Panel para la entrada de datos (pantalla principal)
        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new BorderLayout());
        panelEntrada.setBackground(Color.LIGHT_GRAY);
        panelEntrada.setBounds(10, 10, d.width / 2, 100);
        panelEntrada.add(scrollTexto, BorderLayout.CENTER);
        
        // Panel para mostrar el dato almacenado
        JPanel panelAlmacenado = new JPanel();
        panelAlmacenado.setLayout(new BorderLayout());
        panelAlmacenado.setBackground(Color.LIGHT_GRAY);
        panelAlmacenado.setBounds(10, 120, d.width / 2, 50); // Panel debajo del panel de entrada
        panelAlmacenado.add(scrollAlmacenado, BorderLayout.CENTER);
        
        add(panelEntrada); // Añadir panel de entrada al JFrame
        add(panelAlmacenado); // Añadir panel de dato almacenado al JFrame

        setResizable(false);
        setSize((d.width / 2), 600); // Indica el ancho y alto del recuadro
        setLocationRelativeTo(null); // Mantiene la ventana siempre centrada
        setLayout(null); // Layout nulo, para tener control manual de los tamaños

        // Panel para los botones numéricos
        JPanel panelNumeros = new JPanel(); // Crea el panel para los botones numéricos
        panelNumeros.setLayout(new GridLayout(4, 3)); // Panel para los números
        panelNumeros.setBackground(Color.LIGHT_GRAY); // color del panel
        panelNumeros.setBounds(10, 180, (d.width / 2) - 100, 350); // Ubica debajo de la pantalla y ajusta el ancho
        add(panelNumeros); // Añade el panel de los botones al JFrame

        // Panel para las operaciones
        JPanel panelOperaciones = new JPanel(); // Crea el panel para las operaciones
        panelOperaciones.setLayout(new GridLayout(5, 1)); // Panel para las operaciones
        panelOperaciones.setBackground(Color.LIGHT_GRAY); // color del panel
        panelOperaciones.setBounds((d.width / 2) - 100, 120, 100, 350); // Ubica al lado derecho
        add(panelOperaciones); // Añade el panel de operaciones al JFrame

        String[] botonesNumericos = { 
                "1", "2", "3", 
                "4", "5", "6", 
                "7", "8", "9", 
                "0","C", "=" 
        }; // Son los nombres de los botones numéricos

        for (String texto : botonesNumericos) { // Para el array con los botones mientras haya
            Button button = new Button(texto); // Se crea un boton con el contenido de dicha posición
            button.setFont(new Font("Arial", Font.PLAIN, 20)); // Se describe la fuente y el tamaño
            if(!texto.equals("C")){
                button.setBackground(Color.WHITE); // El color del boton
            }else{
                button.setBackground(Color.GREEN); // El color del boton
            }
            button.addActionListener(new ActionListener() { // Al pulsar el boton
                @Override
                public void actionPerformed(ActionEvent e) {
                    manejarAccionBoton(texto); // Llama al método que escribe el contenido del boton
                }
            });
            panelNumeros.add(button); // Añade el boton al panel de números
        }

        String[] botonesOperaciones = {
                "/", "*", "-",
                "+", ","
        }; // Son los nombres de los botones de operaciones

        for (String texto : botonesOperaciones) { // Para el array con los botones de operaciones mientras haya
            Button button = new Button(texto); // Se crea un boton con el contenido de dicha posición
            button.setFont(new Font("Arial", Font.PLAIN, 20)); // Se describe la fuente y el tamaño
            button.setBackground(Color.WHITE); // El color del boton
            button.addActionListener(new ActionListener() { // Al pulsar el boton
                @Override
                public void actionPerformed(ActionEvent e) {
                    manejarAccionBoton(texto); // Llama al método que escribe el contenido del boton
                }
            });
            panelOperaciones.add(button); // Añade el boton al panel de operaciones
        }
    }

    private void manejarAccionBoton(String texto) {
        if (texto.equals("C")) { // Si se presiona el boton C
            areaTexto.setText(""); // Borra el contenido del JTextArea
        } else if (texto.equals("=")) {
            manejarLaOperacion(); // Llamada para manejar la operación
        } else {
            areaTexto.append(texto); // Añade el texto del botón presionado
        }
    }

    public void manejarLaOperacion() {
        String texto = areaTexto.getText(); // Obtener el texto actual en el JTextArea
    
        if (texto.isEmpty()) { //Si no hay nada escrito
            return; // retorna
        }
    
        try {
            texto = calcularOperacion(texto, "*/"); // comprueba primero si hay multiplicación o división y actualiza el contenido del texto
    
            texto = calcularOperacion(texto, "+-"); // Luego hace las sumas y restas y actualiza el valor de texto
    
            areaTexto.setText(texto); // Escribe el texto en la pantalla
            areaAlmacenado.setText(texto); // Muestra el dato almacenado
        } catch (Exception e) {
            areaTexto.setText("Error"); // Si ocurre un error en el proceso
            areaAlmacenado.setText("Error en la operación"); // Muestra un mensaje de error en el dato almacenado
        }
    }

    private String calcularOperacion(String texto, String operadores) {
        // Expresión regular para encontrar números y operadores
        String regex = "(-?\\d+(?:[.,]\\d+)?)([" + operadores + "])(-?\\d+(?:[.,]\\d+)?)";
    
        // Crear el patrón
        Pattern pattern = Pattern.compile(regex);   // crea un patrón que busca los operadores dados como parámetros
        Matcher matcher = pattern.matcher(texto);   // busca un determinado patrón en el área de texto
    
        // Configurar el formateador para la salida final
        DecimalFormat df = new DecimalFormat("#.##");   // formatea el número
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();  
        symbols.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(symbols);
    
        while (matcher.find()) {
            // Convertir los números a formato estándar para cálculo
            double num1 = Double.parseDouble(matcher.group(1).replace(',', '.')); // obtiene el primer número
            String operador = matcher.group(2); // obtiene el operador
            double num2 = Double.parseDouble(matcher.group(3).replace(',', '.')); // obtiene el segundo número
            double resultado = 0;   // inicializa la variable resultado en 0
    
            // Realizar la operación
            switch (operador) {
                case "*":
                    resultado = num1 * num2;    //Hoy no dormirás sabiendo esto, el num1 se multiplica al num2, inesperado, ¿a que sí?
                    break;
                case "/":
                    if (num2 == 0) {
                        throw new ArithmeticException("División por cero"); // causa de error: Division indeterminada
                    }
                    resultado = num1 / num2;     // para sorpresa de nadie hace una división
                    break;
                case "+":
                    resultado = num1 + num2;    // quizás te impacte saber esto, pero el num1 se suma al num2, impresionante, ¿verdad?
                    break;
                case "-":
                    resultado = num1 - num2;     // el impacto de tan gran línea de código te lena de determinación. Resta dos números. ¡Alucinante!
                    break;
            }
    
            texto = texto.substring(0, matcher.start()) + resultado + texto.substring(matcher.end()); // Reemplaza la operación por el resultado
    
            matcher = pattern.matcher(texto); // Vuelve a buscar el siguiente patrón en el área de texto
        }
    
        return texto.replace('.', ','); // Reemplaza el punto por coma para la visualización final
    }

    public void activarTeclado() {
        areaTexto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode(); // obtiene el código de la tecla
    
                if (isValidKey(keyCode)) {
                    String keyText = "";
    
                    // Convertir las teclas numéricas y operadores a texto
                    if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) {
                        keyText = String.valueOf(keyCode - KeyEvent.VK_NUMPAD0);
                    } else if (keyCode == KeyEvent.VK_ADD) {
                        keyText = "+";
                    } else if (keyCode == KeyEvent.VK_SUBTRACT) {
                        keyText = "-";
                    } else if (keyCode == KeyEvent.VK_MULTIPLY) {
                        keyText = "*";
                    } else if (keyCode == KeyEvent.VK_DIVIDE) {
                        keyText = "/";
                    } else if (keyCode == KeyEvent.VK_DECIMAL) {
                        keyText = ",";
                    } else if (keyCode == KeyEvent.VK_ENTER) {
                        manejarLaOperacion(); // Si se presiona enter, se realiza la operación
                        return;
                    }
    
                    areaTexto.append(keyText); // Añade el texto del botón presionado al área de texto
                }
                e.consume(); // Evita que el teclado realice la acción predeterminada
            }
    
            private boolean isValidKey(int keyCode) {
                return (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9)
                        || keyCode == KeyEvent.VK_ADD
                        || keyCode == KeyEvent.VK_SUBTRACT
                        || keyCode == KeyEvent.VK_MULTIPLY
                        || keyCode == KeyEvent.VK_DIVIDE
                        || keyCode == KeyEvent.VK_ENTER
                        || keyCode == KeyEvent.VK_DECIMAL;
            }
        });
    }

    public static void main(String[] args) {
        CalculadoraPantalla ventana = new CalculadoraPantalla();
        ventana.setVisible(true); // Hace visible la ventana
    }
}
