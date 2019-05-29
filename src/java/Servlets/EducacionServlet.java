/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import AccesoDatos.GlobalException;
import AccesoDatos.NoDataException;
import Control.Control;
import LogicaNegocio.Educacion;
import LogicaNegocio.Skill;
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
 * @author Adriana
 */
@WebServlet(name = "EducacionServlet", urlPatterns = {"/EducacionServlet"})
public class EducacionServlet extends HttpServlet {
    /// Atributos
    Control principal = Control.instance();
    private String educJsonString;
    ArrayList<Educacion> educacion;
    
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
                    educacion = (ArrayList) principal.listarEducacion(usuario);
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(EducacionServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                educJsonString = gson.toJson(educacion);
                try {
                    out.println(educJsonString);
                } finally {
                    out.close();
                }
                break;
            case 2: /// Agregar 
                Educacion educ = null;
                try {
                    educ = new Educacion();
                    educ.setUsuario(request.getParameter("usuario"));
                    educ.setInstitucion(request.getParameter("institucion"));
                    educ.setCarrera(request.getParameter("carrera"));
                    educ.setTitulo(request.getParameter("titulo"));
                    educ.setAnno(request.getParameter("anno"));
                    
                    if(insertarEducacion(educ)){
                        try {
                            //actualiza la lista
                            educacion = (ArrayList)principal.listarEducacion(educ.getUsuario());
                        } catch (GlobalException | NoDataException ex) {
                            out.println("Error al listar.");
                        }

                       educJsonString = gson.toJson(educacion);
                        try {
                            out.println(educJsonString);
                        } finally {
                            out.close();
                        }   
                    }else{
                       out.println("Error al agregar educacion.");  
                    }
                }catch(Exception e){
                    System.out.println("Error "+e);
                }
            break;
            //Elimina el ultimo estudiante en la lista ya que no tienen identificador unico
            case 3:
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    
                    if(eliminarEducacion(id)){
                        out.println("Educacion eliminada.");
                    }else{
                        out.println("Error al eliminar educacion.");
                    }
                } catch(Exception e) {
                    System.out.println(""+e);
                }
            break;
            case 4: // Modifica
                Educacion educedit = null;
                try {
                    educedit = new Educacion();
                    educedit.setId(Integer.parseInt(request.getParameter("id")));
                    educedit.setUsuario(request.getParameter("usuario"));
                    educedit.setInstitucion(request.getParameter("institucion"));
                    educedit.setCarrera(request.getParameter("carrera"));
                    educedit.setTitulo(request.getParameter("titulo"));
                    educedit.setAnno(request.getParameter("anno"));
                    
                    if(modificarEducacion(educedit)){
                        try {
                            /// se modifica la lista
                            educacion = (ArrayList)principal.listarEducacion(educedit.getUsuario());
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(EducacionServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        educJsonString = gson.toJson(educacion);
                        try {
                            out.println(educJsonString);
                        } finally {
                            out.close();
                        } 
                    }else{
                        out.println("Error al editar educacion.");                      
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
    }// </editor-fold>
    
    /// Otros
    public boolean insertarEducacion(Educacion s){
        try{          
            principal.insertarEducacion(s);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean eliminarEducacion(int id){
        try{          
            principal.eliminarEducacion(id);    
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean modificarEducacion(Educacion s){
        try{          
            principal.modificarEducacion(s);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
}
