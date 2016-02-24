package servlets.gameplay;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import servlets.utils.ServletUtils;
import servlets.utils.SessionUtils;
import ws.rummikub.Tile;

@WebServlet(name = "CreateSequenceServlet", urlPatterns = {"/createSequence"})
public class CreateSequenceServlet extends HttpServlet {

    static final String TILES = "tiles";
            
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String tilesJson = request.getParameter(TILES);
        Type tilesListType = new TypeToken<List<Tile>>() {}.getType();
        
        try {
            List<Tile> tiles = new Gson().fromJson(tilesJson, tilesListType);
            
            ServletUtils.getWebService(getServletContext())
                    .createSequence(SessionUtils.getPlayerId(request), tiles);

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
