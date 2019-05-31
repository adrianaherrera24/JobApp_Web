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
@WebServlet(name = "PuestoUsuarioServlet", urlPatterns = {"/PuestoUsuarioServlet"})
public class PuestoUsuarioServlet extends HttpServlet {
    
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
                    
                    /// obtengo la lista desde el bk
                    puestos = (ArrayList) principal.listarPuesto("123");
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(TrabajoServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                puestosJsonString = gson.toJson(puestos);
                try {
                    out.println(puestosJsonString);
                } finally {
                    out.close();
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
    
  
   }

