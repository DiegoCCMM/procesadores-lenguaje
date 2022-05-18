/*****************************************************************/
// File:   Attributes.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   enero 2022
//         Clase única para almacenar los atributos que pueden aparecer en el traductor de adac
//         Se hace como clase plana, por simplicidad. Los atributos que se pueden almacenar
//         se autoexplican con el nombre
/*****************************************************************/

package lib.attributes;
import lib.symbolTable.*;
import lib.errores.*;
import traductor.Token;



public class Attributes implements Cloneable {
    public Symbol.Types type;
    public Symbol.ParameterClass parClass;
    public Symbol referencia_simbolo = null;

    public int valInt;
    public boolean valBool;
    public char valChar;
    public String valString;
    public int tamanyo_vector;


    public Attributes() {
        type = Symbol.Types.UNDEFINED;
        parClass = Symbol.ParameterClass.NONE;
    }

    //Atributo entero no parametro
    public Attributes(int value){
        type = Symbol.Types.INT;
        valInt = value;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo entero parametro
    public Attributes(int value, Symbol.ParameterClass parameter){
        type = Symbol.Types.INT;
        valInt = value;
        parClass = parameter;
    }

    //Atributo char no parametro
    public Attributes(char value){
        type = Symbol.Types.CHAR;
        valChar = value;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo char parametro
    public Attributes(char value, Symbol.ParameterClass parameter){
        type = Symbol.Types.CHAR;
        valChar = value;
        parClass = parameter;
    }

    //Atributo bool no parametro
    public Attributes(boolean value){
        type = Symbol.Types.BOOL;
        valBool = value;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo bool parametro
    public Attributes(boolean value, Symbol.ParameterClass parameter){
        type = Symbol.Types.BOOL;
        valBool = value;
        parClass = parameter;
    }

    //Atributo string
    public Attributes(String value){
        type = Symbol.Types.STRING;
        valString = value;
        parClass = Symbol.ParameterClass.NONE;
    }

    //Atributo vector no parametro
    public Attributes(int tam, Symbol.Types vector_type){
        type = vector_type;
        tamanyo_vector=tam;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo vector parametro
    public Attributes(int tam, Symbol.Types vector_type,Symbol.ParameterClass parameter){
        type = vector_type;
        tamanyo_vector=tam;
        parClass = parameter;
    }
    //Atributo a partir de simbolo
    public Attributes(Symbol s){
        type=s.type;
        parClass=s.parClass;
        if(s instanceof SymbolArray) tamanyo_vector= ((SymbolArray)s).maxInd -((SymbolArray)s).minInd + 1;
        if(s instanceof SymbolInt) valInt= ((SymbolInt)s).value;
        if(s instanceof SymbolChar) valChar= ((SymbolChar)s).value;
        if(s instanceof SymbolBool) valBool= ((SymbolBool)s).value;
        referencia_simbolo=s;
    }


    public Attributes clone() {
    	try {
    		return (Attributes) super.clone();
    	}
    	catch (CloneNotSupportedException e) {
    		return null; 
    	}
    }

    public String toString() {
        String simbolo="";
        if(referencia_simbolo!=null) simbolo= referencia_simbolo.name;
        return
                "type = " + type + "\n" +
                "parClass = " + parClass + "\n" +
                "valInt = " +  valInt + "\n" +
                "valChar = " +  valChar + "\n" +
                "valString = " +  valString + "\n" +
                "valBool = " +  valBool + "\n" +
                "tamañoVector = " +  tamanyo_vector + "\n"+
                "simbolo = " +  simbolo + "\n";
    }



    public static void comprobacion_tipo(Attributes atr_1, Attributes atr_2, boolean operador_booleano,Token t){
        if(operador_booleano){
            atr_1.comprobacion_tipo(Symbol.Types.BOOL,t);
            atr_2.comprobacion_tipo(Symbol.Types.BOOL,t);
        }else{//cualquier otro caso con los operadores aditivos o multiplicativos
            atr_1.comprobacion_tipo(Symbol.Types.INT,t);
            atr_2.comprobacion_tipo(Symbol.Types.INT,t);
        }
    }

    /* Comprobracion del tipo de dato|variable|expresion */
    public boolean comprobacion_tipo(Symbol.Types tipo_esperado,Token t){
        if(this.type != tipo_esperado){
            System.err.println("Atributo ("+ t.beginLine+ ","+t.beginColumn +"): "+ this.toString() ); //TODO: quitar
             ErrorSemantico.deteccion("INCOMPATIBILIDAD DE TIPOS: Se esperaba un dato de tipo "+ tipo_esperado,t);
            this.type=tipo_esperado;
            return(false);
        }
        return(true);
    }


    /* Comprobacion del tipo en operadores relacionales */
    public void comprobacion_mismo_tipo(Attributes sim,Token t){
        if(this.type != sim.type){
            ErrorSemantico.deteccion("No se pueden comparar tipos distintos",t);
        }else if(this.type == Symbol.Types.ARRAY){
            ErrorSemantico.deteccion("No se puede operar con vectores",t);
        }
        if(referencia_simbolo != null){
            referencia_simbolo = null;
        }
    }
    /* Comprobacion del tipo y si es asignable (this es el atributo de la izda de la asignacion) */
    public void comprobacion_asignacion(Attributes sim,Token t){
        //System.err.println("Atributo asignacion: "+ this.toString());

        if(this.type == Symbol.Types.ARRAY){
            ErrorSemantico.deteccion("No se puede asignar a vectores",t);
        }else if(this.type != sim.type){
            ErrorSemantico.deteccion("No se pueden asignar tipos distintos",t);
        }

        if(referencia_simbolo != null){
            referencia_simbolo = null;
        }
    }


    public void comprobaciones_para_vectores(Symbol simbolo_del_factor, Token t){
        if(!(simbolo_del_factor instanceof SymbolArray)) {
            ErrorSemantico.deteccion("Solo se pueden indexar vectores",t);
        }else{
            if(this.comprobacion_tipo(Symbol.Types.INT,t)){   //el indice tiene que ser un entero
                if(simbolo_del_factor != null && simbolo_del_factor instanceof SymbolArray) {   //el simbolo se tiene que poder transformar a un array
                    this.asignar_simbolo_array(simbolo_del_factor); //devolver el atributo del vector, no del indice
                /*if (((SymbolArray) simbolo_del_factor).maxInd > this.valInt && ((SymbolArray) simbolo_del_factor).minInd <= this.valInt) {  //el indice tiene que estar entre el tamaño del vector
                    this.type = ((SymbolArray) simbolo_del_factor).baseType;
                }else{
                    System.out.println("El indice tiene que ser menor que " + ((SymbolArray) simbolo_del_factor).maxInd + " y mayor o igual a "+((SymbolArray) simbolo_del_factor).minInd);
                    System.out.println("El indice recibido es " + this.valInt);
                }*/
                }
            }else{
                ErrorSemantico.deteccion("Para indexar un vector se necesita un valor entero",t);
            }
        }

    }

    /*
        Cuando se realiza un acceso a una componente de un vector el atributo no es de tipo vector sino del tipo que
        contiene el vector
     */
    public void asignar_simbolo_array(Symbol simbolo_del_factor){
        type = ((SymbolArray) simbolo_del_factor).baseType;
        parClass = ((Symbol) simbolo_del_factor).parClass;
        tamanyo_vector=0;
        referencia_simbolo=simbolo_del_factor;
    }

    //TODO: ojo, no asigna parClass!
    public void asignar_simbolo(Symbol simbolo_del_factor){
        this.type = simbolo_del_factor.type;
        Symbol auxiliar_parseo = simbolo_del_factor;
        if(simbolo_del_factor.type == Symbol.Types.INT){
            this.valInt = ((SymbolInt) auxiliar_parseo).value;
        }else if(simbolo_del_factor.type == Symbol.Types.BOOL){
            this.valBool = ((SymbolBool) auxiliar_parseo).value;
        }else if(simbolo_del_factor.type == Symbol.Types.CHAR){
            this.valChar = ((SymbolChar) auxiliar_parseo).value;
        }else if(simbolo_del_factor.type == Symbol.Types.STRING){
            this.valString = ((SymbolArray) auxiliar_parseo).toString();
            this.tamanyo_vector = ((SymbolArray) auxiliar_parseo).maxInd + 1;
        }else if(simbolo_del_factor.type == Symbol.Types.ARRAY){
            tamanyo_vector = ((SymbolArray) auxiliar_parseo).maxInd + 1;
        }else if(simbolo_del_factor.type == Symbol.Types.FUNCTION){
            System.out.println("No se puede utilizar una función sin sus parametros");
        }else if(simbolo_del_factor.type == Symbol.Types.PROCEDURE){
            System.out.println("No se puede utilizar un procedimiento sin sus parametros");
        }else if(simbolo_del_factor.type == Symbol.Types.UNDEFINED){
            System.out.println("El atributo pasa a ser un no definido"); //TODO WARNING
        }
        referencia_simbolo=simbolo_del_factor;
    }

    public void es_parametro_de_accion(SymbolFunction simbolo_funcion, SymbolProcedure simbolo_procedimiento,
                                       int indice, Token t){
        int esperados=-1;
        try {
            if (simbolo_funcion != null) {
                //utilizar simbolo_funcion
                esperados= simbolo_funcion.parList.size();
                Symbol simbolo = simbolo_funcion.parList.get(indice);
                comprobaciones_parametros(simbolo,t);
            } else {
                //utilizar simbolo_procedimiento
                esperados= simbolo_procedimiento.parList.size();
                Symbol simbolo = simbolo_procedimiento.parList.get(indice);
                comprobaciones_parametros(simbolo,t);
            }
        }catch (IndexOutOfBoundsException e){
            ErrorSemantico.deteccion("Se han proporcionado demasiados argumentos (recibidos "+
                     (indice + 1) + ", esperados " + esperados + ")",t);
        }
    }

    private void comprobaciones_parametros(Symbol simbolo, Token t) {
        if (this.type != simbolo.type) {
            ErrorSemantico.deteccion("El parametro con el que se esta invocando a la funcion es erroneo",t);
        }else if(simbolo.parClass == Symbol.ParameterClass.REF && this.referencia_simbolo == null){
             ErrorSemantico.deteccion("Se esperaba una variable, se ha obtenido una constante",t);
        }
    }

    public void returnType_al_tipo(Symbol action){
        if(action instanceof SymbolFunction){
            this.type = ((SymbolFunction)action).returnType;
        }

    }

    public void comprobacion_asignable_leer(Token t){
        //if(this.parClass == Symbol.ParameterClass.VAL) ErrorSemantico.deteccion("No se puede leer un parametro por valor",t);
        if(this.type == Symbol.Types.FUNCTION) ErrorSemantico.deteccion("No se puede leer una funcion",t);
        if(this.type == Symbol.Types.PROCEDURE) ErrorSemantico.deteccion("No se puede leer un procedimiento",t);
        //if(this.type == Symbol.Types.STRING) ErrorSemantico.deteccion("No se puede leer una cadena",t);
    }

    public void comprobacion_escribir(Token t){
         if(this.type == Symbol.Types.ARRAY) ErrorSemantico.deteccion("No se pueden escribir vectores",t);
         if(this.type == Symbol.Types.FUNCTION) ErrorSemantico.deteccion("No se pueden escribir funciones",t);
        if(this.type == Symbol.Types.PROCEDURE) ErrorSemantico.deteccion("No se pueden escribir procedimientos",t);
    }
}
