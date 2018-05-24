import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class FaturacaoApp
{
    private FaturacaoApp () {}
    private static Faturacao fat;
    private static Menu menuprincipal, menucontribuinte, menuempresa, menuregistar;
    
    public static void main(String [] args){
        carregarDados();
        carregarMenus();
        imprimeMenuPrincipal();
        try{
            fat.gravaObj();
        }
        catch(IOException e){
            System.out.println("Não foi possivel gravar os dados");
        }
    }
    
    private static void carregarDados(){
        try{
            fat = Faturacao.leObj();
        }
        catch (IOException e){
            fat = new Faturacao();
            System.out.println("No foi possivel ler os dados \n Erro de leitura");
        }
        catch (ClassNotFoundException e){
            fat = new Faturacao();
            System.out.println("No foi possivel ler os dados \n Ficheiro com formato desconhecido");
        }
        catch(ClassCastException e){
            fat = new Faturacao();
            System.out.println("Não foi possivel ler os dados!\nErro de formato!");
        }
    }
    
    private static void carregarMenus(){
        String [] principal = {"Iniciar Sessao",
                               "Registar"
                               };
                               
        String [] contribuinte = {"Despesas emitidas em meu nome",
                                  "Deduçao acumulada por mim",
                                  "Deduçao acumulada pelo meu agregado familiar",
                                  "Corrigir classificaçao de atividade economica de uma fatura",
                                  "Lista das faturas de uma determinada empresa",
                                  "Total faturado por uma dada empresa num determinado periodo"
                                  };
        
        String [] empresa = {"Criar Fatura",
                             "Facturas por contribuinte num intervalo de datas",
                             "Facturas por contribuinte e por valor decrescente de despesa",
                             "Total faturado por uma dada empresa num determinado periodo"
                             };
        
        String [] registar = {"Registar Contribuinte",
                              "Registar Empresa"
                              };
        
        menuprincipal = new Menu(principal);
        menucontribuinte = new Menu(contribuinte);
        menuempresa = new Menu(empresa);
        menuregistar = new Menu(registar);
    }
    
    private static void imprimeMenuPrincipal(){
        do{
            menuprincipal.executa();
            switch(menuprincipal.getOpcao()){
                case 1: logIn();
                        break;
                case 2: regiUti();
                        break;
            }
        } while (menuprincipal.getOpcao()!= 0);
    }
    
    private static void imprimeMenuContribuinte(){
        do{
            menucontribuinte.executa();
            switch(menucontribuinte.getOpcao()){
                    case 1: minhasDespesas();
                            break;
                    case 2: minhaDeducao();
                            break;
                    case 3: deducaoFamiliar();
                            break;
                    case 4: corrigirClassFat();
                            break;
                    case 5: faturasEmpresa();
                            break;
                    case 6: faturadoEmpresa();
                            break;
                    }
        } while (menucontribuinte.getOpcao()!= 0);
        if (menucontribuinte.getOpcao() == 0) fat.setLogedIn(null);
    }
    
    private static void imprimeMenuEmpresa(){
        do{
            menuempresa.executa();
            switch(menuempresa.getOpcao()){
                case 1: novaFatura();
                        break;
                case 2: faturasTempo();
                        break;
                case 3: faturasValor();
                        break;
                case 4: faturadoEmpresa();
                        break;
            }
        } while (menuempresa.getOpcao()!= 0);
        if (menuempresa.getOpcao() == 0) fat.setLogedIn(null);
    }
    
    private static void logIn(){
        Scanner input = new Scanner(System.in);
        int nif;
        String password;
        
        nif = lerInt("NIF: ");

        System.out.println("Password: ");
        password = input.nextLine();
        
        try{
            fat.iniciaSessao(nif,password);
        }
        catch (SemAutorizacaoException e){
            System.out.println(e.getMessage());
        }
        finally{
            input.close();
        }
        System.out.println("Logged In");
        switch(fat.getTipoUtilizador()){
            case 1: imprimeMenuContribuinte(); break;
            case 2: imprimeMenuEmpresa(); break;
        }
    }
    
    private static void regiUti(){
        int nif;
        String email, nome, morada, password;
        Scanner input = new Scanner(System.in);
        Contribuinte c = null;
        menuregistar.executa();
        
        if(menuregistar.getOpcao()==0){
            System.out.println("Registo Cancelado");
            return;
        }
        
        nif = lerInt("Insira o seu nif: ");
        
        System.out.println("Insira o seu email: ");
        email = input.nextLine();
        
        System.out.println("Insira o seu nome: ");
        nome = input.nextLine();
        
        System.out.println("Insira a sua morada: ");
        morada = input.nextLine();
        
        System.out.println("Insira a sua password: ");
        password = input.nextLine();
        
        switch(menuregistar.getOpcao()){
            case 0: input.close(); return;
            case 1: c = new Individuo(nif); break;
            case 2: c = new Coletivo(nif); break;
        }
        
        c.setEmail(email);
        c.setNome(nome);
        c.setMorada(morada);
        c.setPwd(password);
        
        try{
            fat.registarContribuinte(c);
        }
        catch(ContribuinteExistenteException e){
            System.out.println(e.getMessage());
        }
        finally{
            input.close();
        }
    }
    
    private static void minhasDespesas(){
        try{
            ArrayList<Fatura> lista = fat.despesasEmitidas();
            imprimeFaturas(lista);
            System.out.println("=====================================" + "\n");
        }
        catch(SemAutorizacaoException e){
           System.out.println(e.getMessage());
        }
    }
    
    private static void minhaDeducao(){
    }
    
    private static void deducaoFamiliar(){
    }
    
    private static void corrigirClassFat(){
    }
    
    private static void faturasEmpresa(){
        ArrayList<Fatura> lista = new ArrayList<Fatura>();
        String nomeEmpresa;
        Scanner input = new Scanner(System.in);
        String [] menu = {"Por Data",
                          "Por Valor decrescente"
                          };
        Menu menufaturasempresa = new Menu(menu);
        menufaturasempresa.executa();
        if(menufaturasempresa.getOpcao()==0){
            System.out.println("Processo Cancelado");
            return;
        }
        
        System.out.println("Insira o nome da empresa da qual pretende obter as faturas: ");
        nomeEmpresa = input.nextLine();
        switch(menuregistar.getOpcao()){
            case 0: input.close(); return;
            case 1: lista = fat.facturasEmpresaData(nomeEmpresa); break;
            case 2: lista = fat.facturasEmpresaValor(nomeEmpresa); break;
        }
        
        System.out.println("As faturas correspondentes a empresa " + nomeEmpresa + " sao:" + "\n");
        imprimeFaturas(lista);
        
        input.close();
    }
    
    private static void faturadoEmpresa(){
        String nomeEmpresa;
    }
    
    private static void novaFatura(){
        int nif_cliente, n_atividades;
        double valorFact, taxaImposto;
        String descricao;
        ArrayList<Integer> atividades = new ArrayList<Integer>();
        Scanner input = new Scanner(System.in);
        
        nif_cliente = lerInt("Insira o seu nif do cliente: ");
        
        System.out.println("Insira uma descriçao: ");
        descricao = input.nextLine();
        
        valorFact = lerDouble("Insira o factor de deduçao: ");
        
        n_atividades = lerInt("Insira o nº de atividades economicas diferentes envolvidas: ");
        
        System.out.println("Insira os identificadores dos tipos de atividade economica: ");
        while(n_atividades > 0){
            atividades.add(lerInt(null));
            n_atividades --;
        }
        
        taxaImposto = lerDouble("Insira a taxa de imposto: ");
        
        try{
            fat.novaFactura(nif_cliente, descricao, valorFact, atividades, taxaImposto);
        }
        catch(SemAutorizacaoException e){
            System.out.println(e.getMessage());
        }
        finally{
            input.close();
        }
    }
    
    private static void faturasTempo(){
        ArrayList<Fatura> lista = new ArrayList<Fatura>();
        Scanner input = new Scanner(System.in);
        
        System.out.println("Indique a data inicial: ");
        int ano = lerInt("Ano: ");
        int mes = lerInt("Mês: ");
        int dia = lerInt("Dia: ");
        
        LocalDate inicio = LocalDate.of(ano,mes,dia);
        
        System.out.println("Indique a data final: ");
        ano = lerInt("Ano: ");
        mes = lerInt("Mês: ");
        dia = lerInt("Dia: ");
        
        LocalDate fim = LocalDate.of(ano,mes,dia);
        
        try{
            lista = fat.facturasContribuinteData(inicio,fim);
            System.out.println("As facturas correspondentes a sua empresa no intervalo de tempo dado sao:" + "\n");
            imprimeFaturas(lista);
        }
        catch(SemAutorizacaoException e){
            System.out.println(e.getMessage());
        }
        finally{
            input.close();
        }
    }
    
    private static void faturasValor(){
        Map<Integer,ArrayList<Fatura>> lista = new HashMap<Integer,ArrayList<Fatura>>();
        try{
            lista = fat.facturasContribuinteValor();
            lista.values().forEach(c->imprimeFaturas(c));
        }
        catch(SemAutorizacaoException e){
            System.out.println(e.getMessage());
        }
    }
    
        private static void imprimeAtividades(Fatura f){
            try{
                for(Integer a : f.getListaAtividades()){
                    System.out.println("->" + fat.getNomeAtividade(a) + ";" + "\n");
                }
            }
            catch(SemAutorizacaoException e){
                System.out.println(e.getMessage());
            }
        }
    
        private static void imprimeFaturas(ArrayList<Fatura> lista){
            for(Fatura f : lista){
                System.out.println("Id : " + f.getId() + "\n" +
                                   "Empresa : " + f.getNome() + "\n" +
                                   "Custo : " + f.getValorPagar() + "\n" +
                                   "Atividades : " + "\n");
                imprimeAtividades(f);
            }
        }
        
        private static double lerDouble(String msg){
            Scanner input = new Scanner(System.in);
            double r = 0;
            
            if(msg != null)System.out.println(msg);
            try{
                r= input.nextDouble();
            }
            catch(InputMismatchException e){
                System.out.println("Formato errado!");
                r = lerDouble(msg);
            }
            finally {
                input.close();
            }
            return r;
        }
  
        private static int lerInt(String msg){
            Scanner input = new Scanner(System.in);
            int r = 0;
               
            if(msg != null)System.out.println(msg);
            try{
                r = input.nextInt();
            }
            catch (InputMismatchException e){
                System.out.println("Formato errado!");
                r = lerInt(msg);
            }
            finally{
                input.close();
            }
            return r;
        }
        
}
