/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import AccesoDatos.GlobalException;
import AccesoDatos.NoDataException;
import Control.Control;
import LogicaNegocio.Trabajo;
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
@WebServlet(name = "TrabajoServlet", urlPatterns = {"/TrabajoServlet"})
public class TrabajoServlet extends HttpServlet {
    
    /// Atributos
    Control principal = Control.instance();
    private String trabajosJsonString;
    ArrayList<Trabajo> trabajos;
    
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
                    trabajos = (ArrayList) principal.listarTrabajos(usuario);
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(TrabajoServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                trabajosJsonString = gson.toJson(trabajos);
                try {
                    out.println(trabajosJsonString);
                } finally {
                    out.close();
                }
                break;
            case 2: /// Agregar skill
                Trabajo trabajo = null;
                try {
                    trabajo = new Trabajo();
                    trabajo.setUsuario(request.getParameter("usuario"));
                    trabajo.setEmpresa(request.getParameter("empresa"));
                    trabajo.setPuesto(request.getParameter("puesto"));
                    trabajo.setDescripcion(request.getParameter("descripcion"));
                    trabajo.setAnno_inicio(Integer.parseInt(request.getParameter("ainicio")));
                    trabajo.setAnno_final(Integer.parseInt(request.getParameter("afinal")));
                    
                    if(insertarTrabajos(trabajo)){
                        try {
                            //actualiza la lista
                            trabajos = (ArrayList)principal.listarTrabajos(trabajo.getUsuario());
                        } catch (GlobalException | NoDataException ex) {
                            out.println("Error al listar.");
                        }

                        trabajosJsonString = gson.toJson(trabajos);
                        try {
                            out.println(trabajosJsonString);
                        } finally {
                            out.close();
                        }   
                    }else{
                       out.println("Error al agregar trabajo.");  
                    }
                }catch(Exception e){
                    System.out.println("Error "+e);
                }
            break;
            //Elimina el ultimo estudiante en la lista ya que no tienen identificador unico
            case 3:
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    
                    if(eliminarTrabajos(id)){
                        out.println("Trabajo eliminado.");
                        /*try {
                            /// se modifica la lista
                            trabajos = (ArrayList)principal.listarTrabajos();
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(SkillServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }*/
                    }else{
                        out.println("Error al eliminar trabajo.");
                    }
                } catch(Exception e) {
                    System.out.println(""+e);
                }
            break;
            case 4: // Modifica
                Trabajo trabajoedit = null;
                try {
                    trabajoedit = new Trabajo();
                    trabajoedit.setId(Integer.parseInt(request.getParameter("id")));
                    trabajoedit.setUsuario(request.getParameter("usuario"));
                    trabajoedit.setEmpresa(request.getParameter("empresa"));
                    trabajoedit.setPuesto(request.getParameter("puesto"));
                    trabajoedit.setDescripcion(request.getParameter("descripcion"));
                    trabajoedit.setAnno_inicio(Integer.parseInt(request.getParameter("ainicio")));
                    trabajoedit.setAnno_final(Integer.parseInt(request.getParameter("afinal")));
                    
                    if(modificarTrabajos(trabajoedit)){
                        try {
                            /// se modifica la lista
                            trabajos = (ArrayList)principal.listarTrabajos(trabajoedit.getUsuario());
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(TrabajoServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        trabajosJsonString = gson.toJson(trabajos);
                        try {
                            out.println(trabajosJsonString);
                        } finally {
                            out.close();
                        } 
                    }else{
                        out.println("Error al editar trabajo.");                      
                   }
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
    public boolean insertarTrabajos(Trabajo t){
        try{          
            principal.insertarTrabajos(t);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean eliminarTrabajos(int id){
        try{          
            principal.eliminarTrabajo(id);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean modificarTrabajos(Trabajo t){
        try{          
            principal.modificarTrabajos(t);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
}
