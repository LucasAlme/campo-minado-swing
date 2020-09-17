package br.com.acsul.cm.modelo;

import java.util.ArrayList;
import java.util.List;

public class Campo {

	public void setAberto(boolean aberto) {
		this.aberto = aberto;

		if (aberto == true) {
			notificarObservadores(CampoEvento.ABRIR);
		}
	}

	public boolean isMinado() {
		return minado;
	}

	public int getColuna() {
		return coluna;
	}

	public int getLinha() {
		return linha;
	}

	private boolean minado;
	private boolean aberto;
	private boolean marcado;

	private final int coluna;
	private final int linha;

	private final List<Campo> vizinhos = new ArrayList<>();
	private final List<CampoObservador> observadores = new ArrayList<>();

	public Campo(int coluna, int linha) {
		super();
		this.coluna = coluna;
		this.linha = linha;
	}

	public void registrarObservador(CampoObservador observador) {
		observadores.add(observador);
	}

	private void notificarObservadores(CampoEvento evento) {
		observadores.stream().forEach(o -> o.eventoOcorreu(this, evento));
	}

	boolean adicionarVizinho(Campo vizinho) {
		boolean linhaDiferente = linha != vizinho.linha;
		boolean colunaDiferente = coluna != vizinho.coluna;
		boolean diagonal = linhaDiferente && colunaDiferente;

		int deltaLinha = Math.abs(linha - vizinho.linha);
		int deltaColuna = Math.abs(coluna - vizinho.coluna);
		int deltaGeral = deltaColuna + deltaLinha;

		if (deltaGeral == 1 && !diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else if (deltaGeral == 2 && diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else {
			return false;
		}
	}

	public void alternarMarcacao() {
		if (!aberto) {
			marcado = !marcado;
			if (marcado) {
				notificarObservadores(CampoEvento.MARCAR);
			} else {
				notificarObservadores(CampoEvento.DESMARCAR);
			}
		}
	}

	public boolean abrir() {
		if (!aberto && !marcado) {

			if (minado) {
				notificarObservadores(CampoEvento.EXPLODIR);
				return true;
			}

			setAberto(true);

			if (vizinhancaSegura()) {
				vizinhos.forEach(v -> v.abrir());
			}
			return true;
		} else {
			return false;
		}

	}

	public boolean isMarcado() {
		return marcado;
	}

	public boolean isAberto() {
		return aberto;
	}

	public boolean isFechado() {
		return !isAberto();
	}

	void minar() {
		minado = true;
	}

	public boolean vizinhancaSegura() {

		return vizinhos.stream().noneMatch(v -> v.minado);
	}

	boolean objetivoAlcancado() {
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;
		return desvendado || protegido;
	}

	public int minasNaVizinhanca() {
		return (int)vizinhos.stream().filter(v -> v.minado).count();
	}

	void reiniciar() {
		aberto = false;
		minado = false;
		marcado = false;
	}

}
