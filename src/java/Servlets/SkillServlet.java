/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import AccesoDatos.GlobalException;
import AccesoDatos.NoDataException;
import Control.Control;
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
 * @author Adriana Herrera
 */
@WebServlet(name = "SkillServlet", urlPatterns = {"/SkillServlet"})
public class SkillServlet extends HttpServlet {
    /// Atributos
    Control principal = Control.instance();
    private String skillsJsonString;
    ArrayList<Skill> skills;
    
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
                    skills = (ArrayList) principal.listarSkills();
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(SkillServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                skillsJsonString = gson.toJson(skills);
                try {
                    out.println(skillsJsonString);
                } finally {
                    out.close();
                }
                break;
            case 2: /// Agregar skill
                Skill skill = null;
                try {
                    skill = new Skill();
                    skill.setUsuario(request.getParameter("usuario"));
                    skill.setNombre(request.getParameter("nombre"));
                    skill.setDescripcion(request.getParameter("descripcion"));
                    
                    if(insertarSkills(skill)){
                        try {
                            //actualiza la lista
                            skills = (ArrayList)principal.listarSkills();
                        } catch (GlobalException | NoDataException ex) {
                            out.println("Error al listar.");
                        }

                        skillsJsonString = gson.toJson(skills);
                        try {
                            out.println(skillsJsonString);
                        } finally {
                            out.close();
                        }   
                    }else{
                       out.println("Error al agregar skill.");  
                    }
                }catch(Exception e){
                    System.out.println("Error "+e);
                }
            break;
            //Elimina el ultimo estudiante en la lista ya que no tienen identificador unico
            case 3:
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    
                    if(eliminarSkill(id)){
                        out.println("Skill eliminada.");
                    }else{
                        out.println("Error al eliminar skill.");
                    }
                } catch(Exception e) {
                    System.out.println(""+e);
                }
            break;
            case 4: // Modifica
                Skill skilledit = null;
                try {
                    skilledit = new Skill();
                    skilledit.setId(Integer.parseInt(request.getParameter("id")));
                    skilledit.setUsuario(request.getParameter("usuario"));
                    skilledit.setNombre(request.getParameter("nombre"));
                    skilledit.setDescripcion(request.getParameter("descripcion"));
                    
                    if(modificarSkills(skilledit)){
                        try {
                            /// se modifica la lista
                            skills = (ArrayList)principal.listarSkills();
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(SkillServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        skillsJsonString = gson.toJson(skills);
                        try {
                            out.println(skillsJsonString);
                        } finally {
                            out.close();
                        } 
                    }else{
                        out.println("Error al editar skill.");                      
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
    public boolean insertarSkills(Skill s){
        try{          
            principal.insertarSkills(s);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean eliminarSkill(int id){
        try{          
            principal.eliminarSkill(id);    
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean modificarSkills(Skill s){
        try{          
            principal.modificarSkills(s);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
}
