/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import AccesoDatos.GlobalException;
import AccesoDatos.NoDataException;
import Control.Control;
import LogicaNegocio.Usuario;
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
@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
public class UsuarioServlet extends HttpServlet {
    /// Atributos
    Control principal = Control.instance();
    private String usersJsonString;
    ArrayList<Usuario> users;
    
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
                    /// obtengo la lista desde el bk
                    users = (ArrayList) principal.listarUsuarios();
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(UsuarioServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                usersJsonString = gson.toJson(users);
                try {
                    out.println(usersJsonString);
                } finally {
                    out.close();
                }
                break;
            case 2: /// Agregar skill
                Usuario us = null;
                try {
                    us = new Usuario();
                    us.setId(request.getParameter("id"));
                    us.setNombre(request.getParameter("nombre"));
                    us.setEmail(request.getParameter("email"));
                    us.setPassword(request.getParameter("email"));
                    us.setPrivilegio(request.getParameter("privilegio"));
                    
                    if(insertarUsuarios(us)){
                        try {
                            //actualiza la lista
                            users = (ArrayList)principal.listarUsuarios();
                        } catch (GlobalException | NoDataException ex) {
                            out.println("Error al listar.");
                        }

                        usersJsonString = gson.toJson(users);
                        try {
                            out.println(usersJsonString);
                        } finally {
                            out.close();
                        }   
                    }else{
                       out.println("Error al agregar usuario.");  
                    }
                }catch(Exception e){
                    System.out.println("Error "+e);
                }
            break;
            //Elimina el ultimo estudiante en la lista ya que no tienen identificador unico
            case 3:
                try {
                    String id = request.getParameter("id");
                    
                    if(eliminarUsuario(id)){
                        out.println("Usuario eliminado.");
                    }else{
                        out.println("Error al eliminar usuario.");
                    }
                } catch(Exception e) {
                    System.out.println(""+e);
                }
            break;
            case 4: // Modifica
                Usuario usedit = null;
                try {
                    usedit = new Usuario();
                    usedit.setId(request.getParameter("id"));
                    usedit.setNombre(request.getParameter("nombre"));
                    usedit.setEmail(request.getParameter("email"));
                    usedit.setPassword(request.getParameter("email"));
                    usedit.setPrivilegio(request.getParameter("privilegio"));
                    
                    if(modificarUsuarios(usedit)){
                        try {
                            /// se modifica la lista
                            users = (ArrayList)principal.listarUsuarios();
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(UsuarioServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        usersJsonString = gson.toJson(users);
                        try {
                            out.println(usersJsonString);
                        } finally {
                            out.close();
                        } 
                    }else{
                        out.println("Error al editar usuario.");                      
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
    public boolean insertarUsuarios(Usuario s){
        try{          
            principal.insertarUsuarios(s);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean eliminarUsuario(String id){
        try{          
            principal.eliminarUsuario(id);    
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean modificarUsuarios(Usuario s){
        try{          
            principal.modificarUsuarios(s);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
}
