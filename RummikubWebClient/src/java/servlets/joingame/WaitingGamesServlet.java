package servlets.joingame;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import servlets.utils.ServletUtils;
import ws.rummikub.RummikubWebService;

// Will return a list of gameDetails to show in the waiting games table.
@WebServlet(name = "WaitingGamesServlet", urlPatterns = {"/waitingGames"})
public class WaitingGamesServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        RummikubWebService webService = ServletUtils.getWebService(getServletContext());
        if (webService == null){
            writer.write("Could not connect to the server at this time.");
            return;
        }
        List<String> waitingGames = webService.getWaitingGames();
        
        /*
         * TODO: we need to change the JSON object to structure like:
         * [
         *  {gameName: "game1", humanPlayers, compPlayers, joinedHuman, playerNames}
         *  {gameName: "game2", humanPlayers, compPlayers, joinedHuman, playerNames}
         *  {gameName: "game3", humanPlayers, compPlayers, joinedHuman, playerNames}
         * ]
         */
        String json = new Gson().toJson(waitingGames);
        response.setContentType("application/json");
        writer.write(json);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods.">
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
