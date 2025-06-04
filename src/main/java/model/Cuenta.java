package model;

public class Cuenta {
    private boolean estado;
    private int nroCuenta;
    private double saldo;
    private String banco;

    public Cuenta(boolean estado, int nroCuenta, double saldo, String banco) {
        this.estado = estado;
        this.nroCuenta = nroCuenta;
        this.saldo = saldo;
        this.banco = banco;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getNroCuenta() {
        return nroCuenta;
    }

    public void setNroCuenta(int nroCuenta) {
        this.nroCuenta = nroCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }
}
