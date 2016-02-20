package servlets.joingame;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import servlets.utils.ServletUtils;

@WebServlet(name = "CreateGameServlet", urlPatterns = {"/createGame"})
public class CreateGameServlet extends HttpServlet {
    
    private static final String GAME_NAME = "gameName";
    private static final String HUMAN_PLAYERS_NUM = "humanPlayersNum";
    private static final String COMPUTER_PLAYERS_NUM = "compPlayersNum";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String gameName = request.getParameter(GAME_NAME);
            int humanNum = Integer.parseInt(request.getParameter(HUMAN_PLAYERS_NUM));
            int compNum = Integer.parseInt(request.getParameter(COMPUTER_PLAYERS_NUM));

            ServletUtils.getWebService(getServletContext())
                    .createGame(gameName, humanNum, compNum);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            response.getWriter().write(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
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
