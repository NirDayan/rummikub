package servlets.joingame;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import servlets.utils.ServletConstants;
import servlets.utils.ServletUtils;

// This servlet returns a json that spcifie if the load was successful or not
@WebServlet(name = "LoadGameFormXml", urlPatterns = {"/loadGame"})
public class LoadGameFormXml extends HttpServlet {

    private static final String FILE_CONTENT = "fileContent";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            ServletUtils.getWebService(getServletContext())
                    .createGameFromXML(request.getParameter(FILE_CONTENT));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            response.getWriter().write(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
