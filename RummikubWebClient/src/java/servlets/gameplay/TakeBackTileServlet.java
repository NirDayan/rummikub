package servlets.gameplay;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import servlets.utils.ServletUtils;
import servlets.utils.SessionUtils;

@WebServlet(name = "takeBackTileServlet", urlPatterns = {"/takeBackTile"})
public class TakeBackTileServlet extends HttpServlet {

    private static final String SEQUENCE_INDEX = "sequenceIndex";
    private static final String SEQUENCE_POSITION = "sequencePosition";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int sequenceIndex = Integer.parseInt(request.getParameter(SEQUENCE_INDEX));
        int sequencePosition = Integer.parseInt(request.getParameter(SEQUENCE_POSITION));
        
        try {
            ServletUtils.getWebService(getServletContext())
                    .takeBackTile(SessionUtils.getPlayerId(request), sequenceIndex, sequencePosition);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            response.getWriter().write(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods">
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
