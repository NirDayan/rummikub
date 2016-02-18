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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Collection<Part> parts = request.getParts();
        StringBuilder fileContent = new StringBuilder();
        JsonObject retJson = new JsonObject();

        for (Part part : parts) {
            fileContent.append(readFromInputStream(part.getInputStream()));
        }

        try {
            ServletUtils.getWebService(getServletContext())
                    .createGameFromXML(fileContent.toString());
            retJson.addProperty(ServletConstants.IS_SUCCESS, Boolean.TRUE);
        } catch (Exception ex) {
            retJson.addProperty(ServletConstants.IS_SUCCESS, Boolean.FALSE);
            retJson.addProperty(ServletConstants.ERROR_MSG, ex.getMessage());
        }
        out.write(retJson.toString());
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
