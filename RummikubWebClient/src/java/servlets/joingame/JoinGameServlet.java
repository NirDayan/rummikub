package servlets.joingame;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import servlets.utils.ServletConstants;
import servlets.utils.ServletUtils;

/**
 *
 * @author Lior
 */
@WebServlet(name = "JoinGameServlet", urlPatterns = {"/joinGame"})

public class JoinGameServlet extends HttpServlet {
    private static final String PLAYER_NAME = "playerName";
    private static final String GAME_NAME = "gameName";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        JsonObject retJson = new JsonObject();
        PrintWriter out = response.getWriter();
        String playerName = request.getParameter(PLAYER_NAME);
        String gameName = request.getParameter(GAME_NAME);

        try {
            int playerId = ServletUtils.getWebService(getServletContext())
                    .joinGame(gameName, playerName);

            request.getSession(true).setAttribute(ServletConstants.PLAYER_ID, playerId);
            retJson.addProperty(ServletConstants.IS_SUCCESS, Boolean.TRUE);
        } catch (Exception ex) {
            retJson.addProperty(ServletConstants.IS_SUCCESS, Boolean.FALSE);
            retJson.addProperty(ServletConstants.ERROR_MSG, ex.getMessage());
        }
        out.write(retJson.toString());
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
