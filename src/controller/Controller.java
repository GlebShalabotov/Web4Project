package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import domain.Bericht;
import domain.Person;
import domain.Service;

@WebServlet("/Controller")
public class Controller extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Service model = new Service();
    private ControllerFactory controllerFactory = new ControllerFactory();




    public Controller() {
        super();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String destination = "index.jsp";
        RequestHandler handler = null;
        System.out.println(action);
        if (action != null) {
            try {
                handler = controllerFactory.getController(action, model);
                destination = handler.handleRequest(request, response);

            } catch (NotAuthorizedException exc) {
                List<String> errors = new ArrayList<String>();
                errors.add(exc.getMessage());
                request.setAttribute("errors", errors);
                destination = "index.jsp";
            }
        }
        else{
            try{
                HttpSession session = request.getSession();
                Person man =  (Person) session.getAttribute("user");
                request.setAttribute("user", man);
            }catch (NullPointerException e){
                System.out.println("no user yet");
            }

        }

        if (handler instanceof AsynchroonRequestHandler) {
            response.setContentType("application/json");
            response.getWriter().write(destination);
        }
        else{

            RequestDispatcher view = request.getRequestDispatcher(destination);
            view.forward(request, response);


        }
    }


}
