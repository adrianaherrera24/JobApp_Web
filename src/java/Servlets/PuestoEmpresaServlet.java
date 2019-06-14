/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import AccesoDatos.GlobalException;
import AccesoDatos.NoDataException;
import Control.Control;
import LogicaNegocio.Puesto;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Adriana Herrera
 */
@WebServlet(name = "PuestoEmpresaServlet", urlPatterns = {"/PuestoEmpresaServlet"})
public class PuestoEmpresaServlet extends HttpServlet {
    
    /// Atributos
    Control principal = Control.instance();
    private String puestosJsonString;
    ArrayList<Puesto> puestos;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        // Adding new elements to the ArrayList
        String opcion = (String) request.getParameter("opc");
        
        switch (Integer.parseInt(opcion)) {
            //Listar estudiantes
            case 1:
                try {
                    String usuario = request.getParameter("usuario");
                    /// obtengo la lista desde el bk
                    puestos = (ArrayList) principal.listarPuestoEmpresa(usuario);
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(PuestoEmpresaServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                puestosJsonString = gson.toJson(puestos);
                try {
                    out.println(puestosJsonString);
                } finally {
                    out.close();
                }
                break;
            case 2: /// Agregar skill
                Puesto puesto = null;
                try {
                    puesto = new Puesto();
                    puesto.setId(request.getParameter("id"));
                    puesto.setEmpresa_id(request.getParameter("empresa_id"));
                    puesto.setNombre(request.getParameter("nombre"));              
                    puesto.setArea(request.getParameter("area"));
                    puesto.setDescripcion(request.getParameter("descripcion"));
                    puesto.setRequisitos(request.getParameter("requisitos"));
                    puesto.setHorario(request.getParameter("horario"));
                    puesto.setVigente(0);
                    principal.insertarPuesto(puesto);
                    
                        try {
                            
                            //actualiza la lista
                            puestos = (ArrayList)principal.listarPuesto(puesto.getEmpresa_id());
                        } catch (GlobalException | NoDataException ex) {
                            out.println("Error al listar.");
                        }

                        puestosJsonString = gson.toJson(puestos);
                        try {
                            out.println(puestosJsonString);
                        } finally {
                            out.close();
                        }   
                    
                }catch(Exception e){
                    System.out.println("Error "+e);
                }
            break;
            //Elimina el ultimo estudiante en la lista ya que no tienen identificador unico
            case 3:
                try {
                    String id = request.getParameter("id");
                    
                    principal.eliminarPuesto(id);
                        out.println("Trabajo eliminado.");
                        /*try {
                            /// se modifica la lista
                            puestos = (ArrayList)principal.listarTrabajos();
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(SkillServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }*/
                    
                } catch(Exception e) {
                    System.out.println(""+e);
                }
            break;
            case 4: // Modifica
                Puesto puestoedit = null;
                try {
                    puestoedit = new Puesto();
                    puestoedit.setId(request.getParameter("id"));
                    puestoedit.setEmpresa_id(request.getParameter("empresa_id"));
                    puestoedit.setArea(request.getParameter("area"));
                    puestoedit.setNombre(request.getParameter("nombre"));
                    puestoedit.setDescripcion(request.getParameter("descripcion"));
                    puestoedit.setRequisitos(request.getParameter("ainicio"));
                    puestoedit.setHorario(request.getParameter("horario"));
                    puestoedit.setNombre_empresa(request.getParameter("nombre_empresa"));
                    puestoedit.setLocacion_empresa(request.getParameter("locacion_empresa"));
                    
                    principal.modificarPuesto(puestoedit);
     
                }catch(Exception e){
                    System.out.println(""+e);
                }
            break;
        }  
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
    
    /// Otros
    public boolean insertarPuesto(Puesto t){
        try{          
            principal.insertarPuesto(t);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
   
    public boolean modificarPuesto(Puesto t){
        try{          
            principal.modificarPuesto(t);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
}
;