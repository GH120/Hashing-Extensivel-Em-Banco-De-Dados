class Main {
    
    public static void main(String[] args){
 

        var algoritmo = new HashingExtensivel(2);

        // algoritmo.inserirValor(1, 3);
        // algoritmo.inserirValor(1, 43);
        // algoritmo.inserirValor(1, 23);

        // algoritmo.deletarValor(43);
        
        //Teste busca
        algoritmo.buscarValor(2)
                 .forEach(registro -> {
                    System.out.println(registro.toString());
                });
        
        

        
        try{
            algoritmo.diretorio.armazenarDiretorio("diretorio");
        }
        catch(Exception e){}

    }
}