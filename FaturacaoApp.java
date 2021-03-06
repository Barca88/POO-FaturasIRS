import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.io.FileWriter;
import java.util.stream.Collectors;
import java.util.Collections;

public class FaturacaoApp{
    private static Faturacao fat;
    private static Menu menuprincipal, menucontribuinte, 
                menuempresa, menuregistar, menuadmin;
    
    public static void main(String [] args){
        String file_name = "faturacao_estado";
        carregarDados();
        carregarMenus();
        imprimeMenuPrincipal();
        try{
            fat.gravaObj();
            //fat.log("log.txt",true);
        }
        catch(IOException e){
            System.out.println("Não foi possivel gravar os dados");
        }
        System.out.println("Até á proxima!");
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
                                  "Deduçao acumulada pelo meu agregado familiar",
                                  "Facturas por classificar",
                                  "Corrigir classificaçao de atividade economica de uma fatura",
                                  "Lista das minhas faturas numa determinada empresa"
                                  };
        
        String [] empresa = {"Criar Fatura",
                             "Facturas por contribuinte num intervalo de datas",
                             "Facturas por contribuinte e por valor decrescente de despesa"
                             };
        
        String [] registar = {"Registar Contribuinte",
                              "Registar Empresa",
                              "Registar Admin"
                              };
                              
        String [] admin = {"10 contribuintes que mais gastam",
                           "X empresas que mais facturas passaram e o montante de deduçoes fiscais correspondente a cada",
                           "Total facturado por uma dada empresa num determinado periodo",
                           "Adicionar tipo de Atividade economica"
                          };
        
        menuprincipal = new Menu(principal);
        menucontribuinte = new Menu(contribuinte);
        menuempresa = new Menu(empresa);
        menuregistar = new Menu(registar);
        menuadmin = new Menu(admin);
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
                    case 2: deducaoFamiliar();
                            break;
                    case 3: naoClassificadas();
                            break;
                    case 4: classFat();
                            break;
                    case 5: faturasEmpresa();
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
            }
        } while (menuempresa.getOpcao()!= 0);
        if (menuempresa.getOpcao() == 0) fat.setLogedIn(null);
    }
    
    private static void imprimeMenuAdmin(){
        do{
            menuadmin.executa();
            switch(menuadmin.getOpcao()){
                case 1: top10Cont();
                        break;
                case 2: topXEmpresas();
                        break;
                case 3: faturadoEmpresa();
                        break;
                case 4: adicionaAtividade();
                        break;
            }
        } while (menuadmin.getOpcao()!= 0);
        if (menuadmin.getOpcao() == 0) fat.setLogedIn(null);
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
            case 3: imprimeMenuAdmin(); break;
        }
    }
    
    private static void regiUti(){
        int nif;
        String email, nome, morada, password;
        ArrayList<Integer> descontos = new ArrayList<Integer>();
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
        
        System.out.println("Insira a lista de atividades deduziveis por si/que o seu coletivo pode deduzir (terminando em 0): ");
        scannDescontos(descontos);
        
        
        switch(menuregistar.getOpcao()){
            case 0: input.close(); return;
            case 1: c = new Individuo(nif); break;
            case 2: c = new Coletivo(nif); break;
            case 3: c = new Admin(nif); break;
        }
        
        c.setEmail(email);
        c.setNome(nome);
        c.setMorada(morada);
        c.setPwd(password);
        c.setDescontos(descontos);
        
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
    
        private static void scannDescontos(ArrayList<Integer> descontos){
            Scanner input = new Scanner(System.in);
            int a = -1;
            while(a != 0){
                a = lerInt(null);
                descontos.add(a);
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
    
    private static void deducaoFamiliar(){
        Map<Integer,Double> mapa = new HashMap<Integer,Double>();
        ArrayList<Atividade> atividades = fat.getAtividades().values().stream().collect(Collectors.toCollection(ArrayList::new));
        double deducao = 0;
        
        try{
            mapa = fat.deducaoFiscalFamilia();
            for(Atividade a : atividades){
                if(mapa.containsKey(a.getId())){
                    deducao += mapa.get(a.getId());
                    System.out.println("Classificaçao de Atividade economica: " + a.getNome() 
                                     + "\nDeduçao correspondente a mesma no agregado familiar: " + mapa.get(a.getId()));
                }
            }
            System.out.println("Deduçao total do agregado familiar: " + deducao);
        }
        catch(SemAutorizacaoException e){
           System.out.println(e.getMessage());
        }
    }
    
    private static void naoClassificadas(){
        try{
            List<Fatura> lista = fat.porClassificar(fat.getLogedIn().getNif());
            imprimeFaturas((ArrayList)lista);
        }
        catch(SemAutorizacaoException e){
           System.out.println(e.getMessage());
        }
    }
    
    private static void classFat(){
        long id;
        int atividade;
        
        id = lerLong("Identificador da factura:");
        ArrayList<Atividade> atividades = fat.getActPossiveis(id);
        imprimeAtividades(atividades);
        
        atividade = lerInt("Indentificador do tipo de atividade economica pretendido:");
        
        try{
            fat.corrigeClassificaFatura(id,atividade);
        }
        catch(SemAutorizacaoException e){
           System.out.println(e.getMessage());
        }
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
        switch(menufaturasempresa.getOpcao()){
            case 0: input.close(); return;
            case 1: lista = fat.facturasEmpresaData(nomeEmpresa); break;
            case 2: lista = fat.facturasEmpresaValor(nomeEmpresa); break;
        }
        
        System.out.println("As faturas correspondentes a empresa " + nomeEmpresa + " sao:" + "\n");
        imprimeFaturas(lista);
        
        input.close();
    }
    
    private static void faturadoEmpresa(){
        int nifEmpresa;
        double cont;
        Scanner input = new Scanner(System.in);
        nifEmpresa = lerInt("Insira o nif da empresa pretendida:");
        
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
            cont = fat.totalFaturadoColetivo(nifEmpresa,inicio,fim);
            System.out.println("A empresa" + fat.getUsers().get(nifEmpresa).getNome() + "facturou" + cont + "no total."); 
        }
        catch(SemAutorizacaoException e){
           System.out.println(e.getMessage());
        }
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
        
        valorFact = lerDouble("Insira o valor sem IVA: ");
        
        n_atividades = lerInt("Insira o nº de atividades economicas diferentes envolvidas: ");
        
        System.out.println("Insira os identificadores dos tipos de atividade economica: ");
        while(n_atividades > 0){
            atividades.add(lerInt(null));
            n_atividades --;
        }
        
        taxaImposto = lerDouble("Insira a taxa de imposto(0 a 100): ");
        
        try{
            fat.novaFactura(nif_cliente, descricao, valorFact, taxaImposto);
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
    
    private static void top10Cont(){
        try{
            for(Contribuinte c : fat.top10Cont()){
                System.out.println("NIF: " + c.getNif() + "\nNome: " + c.getNome() + "\nTotal Gastos: " + c.getGastos());
            }
        }
        catch(SemAutorizacaoException e){
            System.out.println(e.getMessage());
        }
    }
    
    private static void topXEmpresas(){
        Scanner input = new Scanner(System.in);
        int x;
        
        x = lerInt("Quantas empresas pretende verificar?");
        
        try{
            for(Contribuinte c : fat.topXEmpresas(x)){
                
                System.out.println("NIF: " + c.getNif() 
                                           + "\nNome: " + c.getNome() 
                                           + "\nDeduçao total da Empresa: " + fat.deducaoEmpresa(c.getNif()));
            }
        }
        catch(SemAutorizacaoException e){
            System.out.println(e.getMessage());
        }
    }
    
    private static void adicionaAtividade(){
        String nome;
        Double deducao;
        int max;
        Scanner input = new Scanner(System.in);
        
        System.out.println("Insira o nome do tipo de atividade economica: ");
        nome = input.nextLine();
        
        deducao = lerDouble("Insira o factor de deducao da atividade economica em questao: ");
        
        max = lerInt("Insira o maximo de deducao possivel de se obter atraves da atividade economica em questao: ");
        
        try{
            fat.criaAtividade(nome, deducao, max);
        }
        catch(SemAutorizacaoException e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     *  ===================================== Funçoes Auxiliares =================================
     */
    
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
        
        private static void imprimeAtividades(ArrayList<Atividade> lista){
            for(Atividade a : lista){
                System.out.println("Id : " + a.getId() + "\n" +
                                   "Nome : " + a.getNome());
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
        
        private static long lerLong(String msg){
            Scanner input = new Scanner(System.in);
            long r = 0;

            System.out.println(msg);
            try{
                r = input.nextLong();
            }
            catch (InputMismatchException e){
                System.out.println("Formato errado!");
                r = lerLong(msg);
            }
            finally{
                input.close();
            }
            return r;
        }
        
}
