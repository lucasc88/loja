package br.com.centraldaassinatura.loja.model;

public enum NaturesLegals {

	EMPRESA_PUBLICA("201-1", "Empresa Pública"),
	SOCIEDADE_ECONOMIA_MISTA("203-8", "Sociedade de Economia Mista"),
	SOCIEDADE_ANONIMA_ABERTA("204-6", "Sociedade Anônima Aberta"),
	SOCIEDADE_ANONIMA_FECHADA("205-4", "Sociedade Anônima Fechada"),
	SOCIEDADE_EMPRESARIA_LIMITADA("206-2", "Sociedade Empresária Limitada"),
	SOCIEDADE_EMPRESARIA_EM_NOME_COLETIVO("207-0", "Sociedade Empresária em Nome Coletivo"),
	SOCIEDADE_EMPRESARIA_EM_CANDIDATA_SIMPLES("208-9", "Sociedade Empresária em Comandita Simples"),
	SOCIEDADE_EMPRESARIA_EM_CANDIDATA_ACOES("209-7", "Sociedade Empresária em Comandita por Ações"),
	SOCIEDADE_EMPRESARIA_EM_CONTA_PARTICIPACAO("212-7", "Sociedade em Conta de Participação"),
	COOPERATIVA("214-3", "Cooperativa"),
	CONSORCIO_SOCIEDADES("215-1", "Consórcio de Sociedades"),
	GRUPO_SOCIEDADES("216-0", "Grupo de Sociedades"),
	SOCIEDADE_ESTRANGEIRA("217-8", "Estabelecimento, no Brasil, de Sociedade Estrangeira"),
	ARGENTINO_BRASILEIRA("219-4", "Estabelecimento, no Brasil, de Empresa Binacional Argentino-Brasileira"),
	EMPRESA_DOMICILIADA_EXTERIOR("221-6", "Empresa Domiciliada no Exterior"),
	FUNDO_INVESTIMENTO("222-4", "Clube/Fundo de Investimento"),
	SOCIEDADE_SIMPLES_PURA("223-2", "Sociedade Simples Pura"),
	SOCIEDADE_SIMPLES_LIMITADA("224-0", "Sociedade Simples Limitada");

    private String cod, name;

    NaturesLegals(String cod, String name){ 
    	this.cod = cod;
    	this.name = name;
    }

	public String getCod() {
		return cod;
	}

	public String getName() {
		return name;
	}
    
}
