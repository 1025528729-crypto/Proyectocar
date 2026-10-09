package com.mycompany.rentcar.model;

import com.mycompany.rentcar.config.Conexion;
import com.mycompany.rentcar.presenter.MarcaItem;
import java.sql.*;
import java.util.*;

/** Acceso a datos de autos y motos. Conserva compatibilidad con el esquema antiguo. */
public class VehiculoDAO {
    public VehiculoDAO(){ asegurarColumnasNuevas(); }

    private void asegurarColumnasNuevas(){
        String[][] columnas={
            {"tipo","VARCHAR(12) NOT NULL DEFAULT 'AUTO'"},{"anio","INT NOT NULL DEFAULT 0"},
            {"color","VARCHAR(40) DEFAULT ''"},{"transmision","VARCHAR(40) DEFAULT ''"},
            {"combustible","VARCHAR(40) DEFAULT ''"},{"capacidad","INT NOT NULL DEFAULT 5"},
            {"kilometraje","INT NOT NULL DEFAULT 0"},{"puertas","INT NOT NULL DEFAULT 4"},{"categoria","VARCHAR(60) DEFAULT ''"},
            {"descripcion","TEXT"},{"ciudad","VARCHAR(100) DEFAULT ''"},
            {"disponible","TINYINT(1) NOT NULL DEFAULT 1"}
        };
        try(Connection c=Conexion.getConexion()){
            DatabaseMetaData md=c.getMetaData();
            Set<String> existentes=new HashSet<>();
            try(ResultSet rs=md.getColumns(c.getCatalog(),null,"vehiculos",null)){
                while(rs.next()) existentes.add(rs.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
            }
            for(String[] col:columnas) if(!existentes.contains(col[0])){
                try(Statement st=c.createStatement()){st.executeUpdate("ALTER TABLE vehiculos ADD COLUMN "+col[0]+" "+col[1]);}
                catch(SQLException ex){System.err.println("No se pudo agregar columna "+col[0]+": "+ex.getMessage());}
            }
        }catch(SQLException ex){System.err.println("No se pudieron verificar las columnas extendidas de vehiculos: "+ex.getMessage());}
    }

    public boolean insertar(Vehiculo v){
        String sql="INSERT INTO vehiculos (placa,marca,modelo,precio_por_dia,foto,tipo,anio,color,transmision,combustible,capacidad,kilometraje,puertas,categoria,descripcion,ciudad,disponible) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try(Connection c=Conexion.getConexion();PreparedStatement p=c.prepareStatement(sql)){
            setDatos(p,v); return p.executeUpdate()>0;
        }catch(SQLException e){System.err.println("Error al insertar vehículo: "+e.getMessage());return false;}
    }
    public boolean actualizar(Vehiculo v){
        String sql="UPDATE vehiculos SET marca=?,modelo=?,precio_por_dia=?,foto=?,tipo=?,anio=?,color=?,transmision=?,combustible=?,capacidad=?,kilometraje=?,puertas=?,categoria=?,descripcion=?,ciudad=?,disponible=? WHERE placa=?";
        try(Connection c=Conexion.getConexion();PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,v.getMarca());p.setString(2,v.getModelo());p.setDouble(3,v.getPrecioPorDia());p.setString(4,v.getFoto());
            p.setString(5,v.getTipo());p.setInt(6,v.getAnio());p.setString(7,v.getColor());p.setString(8,v.getTransmision());p.setString(9,v.getCombustible());p.setInt(10,v.getCapacidad());p.setInt(11,v.getKilometraje());p.setInt(12,v.getPuertas());p.setString(13,v.getCategoria());p.setString(14,v.getDescripcion());p.setString(15,v.getCiudad());p.setBoolean(16,v.isDisponible());p.setString(17,v.getPlaca());
            return p.executeUpdate()>0;
        }catch(SQLException e){System.err.println("Error al actualizar vehículo: "+e.getMessage());return false;}
    }
    private void setDatos(PreparedStatement p,Vehiculo v)throws SQLException{
        p.setString(1,v.getPlaca());p.setString(2,v.getMarca());p.setString(3,v.getModelo());p.setDouble(4,v.getPrecioPorDia());p.setString(5,v.getFoto());p.setString(6,v.getTipo());p.setInt(7,v.getAnio());p.setString(8,v.getColor());p.setString(9,v.getTransmision());p.setString(10,v.getCombustible());p.setInt(11,v.getCapacidad());p.setInt(12,v.getKilometraje());p.setInt(13,v.getPuertas());p.setString(14,v.getCategoria());p.setString(15,v.getDescripcion());p.setString(16,v.getCiudad());p.setBoolean(17,v.isDisponible());
    }
    public boolean eliminar(String placa){try(Connection c=Conexion.getConexion();PreparedStatement p=c.prepareStatement("DELETE FROM vehiculos WHERE placa=?")){p.setString(1,placa);return p.executeUpdate()>0;}catch(SQLException e){System.err.println("Error al eliminar vehículo: "+e.getMessage());return false;}}
    public List<Vehiculo> listar(){return consultar("SELECT * FROM vehiculos ORDER BY disponible DESC, marca, modelo",null);}
    public List<Vehiculo> listarPorMarca(String marca){return consultar("SELECT * FROM vehiculos WHERE LOWER(marca)=LOWER(?) ORDER BY marca,modelo",marca);}
    public List<Vehiculo> listarPorTipo(String tipo){return consultar("SELECT * FROM vehiculos WHERE UPPER(tipo)=UPPER(?) ORDER BY marca,modelo",tipo);}
    private List<Vehiculo> consultar(String sql,String parametro){
        List<Vehiculo> lista=new ArrayList<>();
        try(Connection c=Conexion.getConexion();PreparedStatement p=c.prepareStatement(sql)){
            if(parametro!=null)p.setString(1,parametro);
            try(ResultSet r=p.executeQuery()){while(r.next())lista.add(mapear(r));}
        }catch(SQLException e){System.err.println("Error al consultar vehículos: "+e.getMessage());}
        return lista;
    }
    private Vehiculo mapear(ResultSet r)throws SQLException{
        Vehiculo v=new Vehiculo();v.setPlaca(r.getString("placa"));v.setMarca(r.getString("marca"));v.setModelo(r.getString("modelo"));v.setPrecioPorDia(r.getDouble("precio_por_dia"));v.setFoto(r.getString("foto"));
        v.setTipo(r.getString("tipo"));v.setAnio(r.getInt("anio"));v.setColor(r.getString("color"));v.setTransmision(r.getString("transmision"));v.setCombustible(r.getString("combustible"));v.setCapacidad(r.getInt("capacidad"));v.setKilometraje(r.getInt("kilometraje"));v.setPuertas(r.getInt("puertas"));v.setCategoria(r.getString("categoria"));v.setDescripcion(r.getString("descripcion"));v.setCiudad(r.getString("ciudad"));v.setDisponible(r.getBoolean("disponible"));return v;
    }
    public List<MarcaItem> obtenerMarcas(){
        List<MarcaItem> marcas=new ArrayList<>();String sql="SELECT DISTINCT marca FROM vehiculos WHERE marca IS NOT NULL ORDER BY marca";
        try(Connection c=Conexion.getConexion();PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()){
            int id=1;while(r.next()){String marca=r.getString("marca");marcas.add(new MarcaItem(id++,marca,marca.toLowerCase(Locale.ROOT).trim()+".jpg"));}
        }catch(SQLException e){System.err.println("Error al obtener marcas: "+e.getMessage());}return marcas;
    }
    public Vehiculo buscarPorPlaca(String placa){List<Vehiculo> l=consultar("SELECT * FROM vehiculos WHERE placa=?",placa);return l.isEmpty()?null:l.get(0);}
}
