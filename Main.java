class Main {
    
    public static void main(String[] args){
        
        // Bucket bucket = new Bucket(29);

        // bucket.adicionarRegistro(1,2002);
        // bucket.adicionarRegistro(3,2022);
        // bucket.adicionarRegistro(0,2012);

        // bucket.salvarBucket();
        // bucket.salvarBucket();
        // bucket.salvarBucket();

        var algoritmo = new HashingExtensivel(2);

        algoritmo.inserirValor(1, 22);



        
        try{
            algoritmo.diretorio.armazenarDiretorio("diretorio");
        }
        catch(Exception e){}

    }
}