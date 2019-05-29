/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import AccesoDatos.GlobalException;
import AccesoDatos.NoDataException;
import Control.Control;
import LogicaNegocio.Referencia;
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
@WebServlet(name = "ReferenciaServlet", urlPatterns = {"/ReferenciaServlet"})
public class ReferenciaServlet extends HttpServlet {
    /// Atributos
    Control principal = Control.instance();
    private String refsJsonString;
    ArrayList<Referencia> refs;
    
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
                    refs = (ArrayList) principal.listaReferencia(usuario);
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(ReferenciaServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                refsJsonString = gson.toJson(refs);
                try {
                    out.println(refsJsonString);
                } finally {
                    out.close();
                }
                break;
            case 2: /// Agregar skill
                Referencia refss = null;
                try {
                    refss = new Referencia();
                    refss.setUsuario(request.getParameter("usuario"));
                    refss.setNombre(request.getParameter("nombre"));
                    refss.setTelefono(request.getParameter("telefono"));
                    refss.setEmail(request.getParameter("email"));
                    refss.setDescripcion(request.getParameter("descripcion"));
                    
                    if(insertarReferencia(refss)){
                        try {
                            //actualiza la lista
                            refs = (ArrayList)principal.listaReferencia(refss.getUsuario());
                        } catch (GlobalException | NoDataException ex) {
                            out.println("Error al listar.");
                        }

                        refsJsonString = gson.toJson(refs);
                        try {
                            out.println(refsJsonString);
                        } finally {
                            out.close();
                        }   
                    }else{
                       out.println("Error al agregar referencia.");  
                    }
                }catch(Exception e){
                    System.out.println("Error "+e);
                }
            break;
            //Elimina el ultimo estudiante en la lista ya que no tienen identificador unico
            case 3:
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    
                    if(eliminarReferencia(id)){
                        out.println("Referencia eliminada.");
                    }else{
                        out.println("Error al eliminar referencia.");
                    }
                } catch(Exception e) {
                    System.out.println(""+e);
                }
            break;
            case 4: // Modifica
                Referencia refsedit = null;
                try {
                    refsedit = new Referencia();
                    refsedit.setUsuario(request.getParameter("usuario"));
                    refsedit.setNombre(request.getParameter("nombre"));
                    refsedit.setTelefono(request.getParameter("telefono"));
                    refsedit.setEmail(request.getParameter("email"));
                    refsedit.setDescripcion(request.getParameter("descripcion"));
                    
                    if(modificarReferencias(refsedit)){
                        try {
                            /// se modifica la lista
                            refs = (ArrayList)principal.listaReferencia(refsedit.getUsuario());
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(ReferenciaServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        refsJsonString = gson.toJson(refs);
                        try {
                            out.println(refsJsonString);
                        } finally {
                            out.close();
                        } 
                    }else{
                        out.println("Error al editar referencia.");                      
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
    public boolean insertarReferencia(Referencia s){
        try{          
            principal.insertarReferencia(s);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean eliminarReferencia(int id){
        try{          
            principal.eliminarReferencia(id);    
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean modificarReferencias(Referencia s){
        try{          
            principal.modificarReferencias(s);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
}
