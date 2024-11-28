import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.regex.*;
import javax.swing.*;

public class CalculadoraPantalla extends JFrame{

    // Creamos una variable global para el JTextArea
    private JTextArea areaTexto;
    private boolean tecladoOn = true;

    public CalculadoraPantalla() {
        super("Calculadora"); // nombre de la pestaña

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
        
        pantallaSecundaria.add(scrollTexto, BorderLayout.CENTER); // panel secundario donde se escribe los números

        add(pantallaSecundaria); // Añade el panel de la pantalla al JFrame
        setResizable(false);
        setSize((d.width / 2), 600); // Indica el ancho y alto del recuadro
        setLocationRelativeTo(null); // Mantiene la ventana siempre centrada
        setLayout(null); // Layout nulo, para tener control manual de los tamaños

        // Panel para los botones
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 4)); // Crea el panel para los botones de 5 lineas con 4 botones
        mainPanel.setBackground(Color.LIGHT_GRAY); // color del panel
        mainPanel.setBounds(10, 120,(d.width / 2), 500); // Ubica el panel de los botones debajo de la pantalla
        add(mainPanel); // Añade el panel de los botones al JFrame

        String[] botones = {
                "1", "2", "3", "/", "4", "5", "6", "*",
                "7", "8", "9", "-", "0", "+", "=", "C"
        }; // Son los nombres de los
        for (String texto : botones) { // Para el array con los botones mientras haya
            Button button = new Button(texto); // Se crea un boton con el contenido de dicha posición
            button.setFont(new Font("Arial", Font.PLAIN, 20)); // Se describe la fuente y el tamaño
            button.setBackground(Color.WHITE); // El color del boton
            button.addActionListener(new ActionListener() { // Al pulsar el boton
                @Override
                public void actionPerformed(ActionEvent e) {
                    manejarAccionBoton(texto); // Llama al método que escribe el contenido del boton
                }
            });
            mainPanel.add(button); // Añade el boton al panel
        }
    }

    private void manejarAccionBoton(String texto) {
        if (texto.equals("C")) { // Si se presiona el boton C
            areaTexto.setText(""); // Borra el contenido del JTextArea
        } else if (texto.equals("=")) {
            manejarLaOperacion(); // Llamada para manejar la operación (si es necesario implementarla)
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
            texto = calcularOperacion(texto, "*/"); // comprueba primero si hay multiplicacion o división y actualiza el contenido del texto
    
            texto = calcularOperacion(texto, "+-"); // Luego hace las sumas y restas y actualiza el valor de texto
    
            areaTexto.setText(texto); // Escribe el texto en la pantalla
        } catch (Exception e) {
            areaTexto.setText("Error"); // Si peta en el proceso
        }
    }

    private String calcularOperacion(String texto, String operadores) {
        // Expresión regular para encontrar números y operadores
        String regex = "(-?\\d+(?:[.,]\\d+)?)([" + operadores + "])(-?\\d+(?:[.,]\\d+)?)";
    
        // Crear el patrón
        Pattern pattern = Pattern.compile(regex);   // crea un patron que busca los operadores dados como parámetros
        Matcher matcher = pattern.matcher(texto);   // busca un determinado patrón en el area de texto
    
        // Configurar el formateador para la salida final
        DecimalFormat df = new DecimalFormat("#.##");   //formatea el caracter .
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();  //
        symbols.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(symbols);
    
        while (matcher.find()) {
            // Convertir los números a formato estándar para cálculo
            double num1 = Double.parseDouble(matcher.group(1).replace(',', '.')); // obtiene el grupo de numeros antes del operador
            String operador = matcher.group(2); //obtiene el operador
            double num2 = Double.parseDouble(matcher.group(3).replace(',', '.')); // obtiene el grupo de numeros que sigue al operador
            double resultado = 0;   // inaugura la variable resultado en 0
    
            // Realizar la operación
            switch (operador) {
                case "*":
                    resultado = num1 * num2;    //Hoy no dormirás sabiendo esto, el num1 se multiplica al num2, inesperado, ¿a que sí?
                    break;
                case "/":
                    if (num2 == 0) {
                        throw new ArithmeticException("División por cero"); // causa de error: Division indeterminada
                    }
                    resultado = num1 / num2;    // para sorpresa de nadie hace una división
                    break;
                case "+":
                    resultado = num1 + num2;    // quizás te impacte saber esto, pero el num1 se suma al num2, impresionante, ¿verdad?
                    break;
                case "-":
                    resultado = num1 - num2;    // el impacto de tan gran línea de código te lena de determinación. Resta dos números. ¡Alucinante!
                    break;
            }
    
            texto = texto.substring(0, matcher.start()) + resultado + texto.substring(matcher.end()); // reescribe al area de texto con el resultado
    
            matcher = pattern.matcher(texto); // vuelva a buscar el siguiente patron en el area de texto
        }
    
        return texto.replace('.', ','); // Reemplaza el punto por coma para la visualización final
    }
    

    public void activarTeclado() {
        areaTexto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode(); // obtiene el codigo de la tecla
    
                if (isValidKey(keyCode)) {
                    String keyText = "";
    
                    // Convertir las teclas numéricas y operadores a texto
                    if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) {
                        keyText = String.valueOf(keyCode - KeyEvent.VK_NUMPAD0); // Números del teclado numérico (numpad)
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
                    }else if (keyCode == KeyEvent.VK_ENTER) {
                        manejarLaOperacion(); // Procesar la operación al presionar Intro
                        return; // Salir del método sin añadir más texto
                    }
    
                    areaTexto.append(keyText); // Añadir el texto correspondiente al área
                }
                e.consume(); // Consumir el evento para evitar comportamiento adicional
            }
    
            // Verificar si la tecla pertenece al teclado numérico o es un operador válido
            private boolean isValidKey(int keyCode) {
                return (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) // Teclas numéricas
                        || keyCode == KeyEvent.VK_ADD // Suma
                        || keyCode == KeyEvent.VK_SUBTRACT // Resta
                        || keyCode == KeyEvent.VK_MULTIPLY // Multiplicación
                        || keyCode == KeyEvent.VK_DIVIDE // División
                        || keyCode == KeyEvent.VK_ENTER // Intro para igual
                        || keyCode == KeyEvent.VK_DECIMAL; // punto para la coma
            }
        });
    }
    
    public static void main(String[] args) {
            CalculadoraPantalla ventana = new CalculadoraPantalla();    // Crea el objeto calculadora
            ventana.setVisible(true);   // declara visible la ventana
    }
}
