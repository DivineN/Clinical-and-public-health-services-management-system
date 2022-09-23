/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Dao.AppointmentDao;
import Dao.ConsultationDao;
import Dao.DoctorsDao;
import Dao.OperationDao;
import Dao.UserDao;
import Domain.Appointment;
import Domain.Consultation;
import Domain.User;
import Domain.Doctors;
import Domain.Hospital;
import Domain.Operation;
import Domain.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Rugwiro
 */
@ManagedBean
@SessionScoped
public class HospitalModel {

    private List<Doctors> docList;
    private Doctors doctors = new Doctors();
    DoctorsDao docDao = new DoctorsDao();
    private Doctors docdetails = new Doctors();
    private Hospital loadHospitals = new Hospital();

    List<Appointment> appList2 = new AppointmentDao().view("from Appointment a WHERE a.doctors.hospital.Id='" + loadName2() + "'");
    List<Operation> opList2 = new OperationDao().view("from Operation a WHERE a.doctors.hospital.Id='" + loadName2() + "'");
    List<Appointment> appList3 = new AppointmentDao().view("from Appointment a WHERE a.doctors.hospital.Id='" + loadName2() + "' GROUP BY a.patients.Id");
    private List<User> userList = new UserDao().findAll(User.class);
    String NewUserName, OldPass, NewPass, ConfNewPass;

    private User u = new User();

    @PostConstruct
    public void init() {
        loadName2();
        docList = new DoctorsDao().view("from Doctors a WHERE a.hospital.Id='" + loadName2() + "'");
    }

    public Integer loadName2() {
        User x = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
        loadHospitals = x.getHospital();
        Integer IdNum = loadHospitals.getId();
        return IdNum;
    }

    public void createDoctor() {

        User x = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
        doctors.setHospital(x.getHospital());
        new DoctorsDao().save(doctors);
        u.setPassword(u.getPassword());
        u.setAccess("Doctor");
        u.setDoctors(doctors);
        new UserDao().save(u);

        u = new User();
        doctors = new Doctors();

        docList = new DoctorsDao().view("from Doctors a WHERE a.hospital.Id='" + loadName2() + "'");
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Doctor Registered!", null));
    }

    public void updateDoctor() {

        docDao.update(docdetails);
        docdetails = new Doctors();
        docList = new DoctorsDao().view("from Doctors a WHERE a.hospital.Id='" + loadName2() + "'");
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Updated!", null));
    }

    public String fetchItems(final Doctors doc) {
        this.docdetails = doc;
        return "EditDoctor.xhtml?faces-redirect=true";
    }

    public void deleteDoctor() {
        docDao.delete(this.docdetails);
        docList = new DoctorsDao().view("from Doctors a WHERE a.hospital.Id='" + loadName2() + "'");
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Deleted!", null));
    }

    public void fetchAndShow(final Doctors doc) {
        this.docdetails = doc;
        RequestContext.getCurrentInstance().execute("PF('delete').show()");
    }

    public void updateInfo() {

        userList.forEach((us) -> {
            if (us.getAccess().matches("Hospital") && loadHospitals.getId().equals(us.getHospital().getId())) {

                if (!OldPass.equals(us.getPassword())) {
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Old Password is Incorrect!", null));

                } else if (!NewPass.equals(ConfNewPass)) {
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password do not match!", null));

                } else {

                    u.setId(us.getId());
                    u.setAccess(us.getAccess());
                    u.setDoctors(us.getDoctors());
                    u.setPatients(us.getPatients());
                    u.setHospital(us.getHospital());
                    u.setUsername(us.getUsername());
                    u.setPassword(NewPass);
                    new UserDao().update(u);
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password Changed!", null));

                }

            }

        });

    }

//    public void createOneConsultationPDF() {
//        try {
//
//            FacesContext context = FacesContext.getCurrentInstance();
//            Document document = new Document();
//            Rectangle rect = new Rectangle(20, 20, 800, 800);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            PdfWriter writer = PdfWriter.getInstance(document, baos);
//            writer.setBoxSize("art", rect);
//            document.setPageSize(rect);
//            if (!document.isOpen()) {
//                document.open();
//            }
//            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\Dashboard\\");
//            path = path.substring(0, path.indexOf("\\build"));
//            path = path + "\\web\\Dashboard\\Admin\\assets\\img\\logo.png";
//            Image image = Image.getInstance(path);
//            image.scaleAbsolute(75, 25);
//            image.setAlignment(Element.ALIGN_LEFT);
//            Paragraph title = new Paragraph();
//            //BEGIN page
//            title.add(image);
//            title.add("\n Non-Communicable Diseases System");
//            title.add("\n P.O. Box 7162 Kigali, Rwanda");
//            title.add("\n Email: info@rbc.gov.rw");
//            document.add(title);
//
//            Font font0 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.UNDERLINE);
//            Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
//            Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
//            Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
//            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
//            Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
//            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE);
//
//            document.add(new Paragraph("\n"));
//            Paragraph para = new Paragraph("Consultations Report List in "+loadHospitals.getName(), font0);
//            para.setAlignment(Element.ALIGN_CENTER);
//            document.add(para);
//            document.add(new Paragraph("\n"));
//            PdfPTable table = new PdfPTable(5);
//            table.setWidthPercentage(100);
//
//            PdfPCell cell1 = new PdfPCell(new Phrase("ID", font2));
//            cell1.setBorder(Rectangle.BOX);
//            table.addCell(cell1);
//
//            PdfPCell cell2 = new PdfPCell(new Phrase("Patient's Name", font2));
//            cell2.setBorder(Rectangle.BOX);
//            table.addCell(cell2);
//
//            PdfPCell cell3 = new PdfPCell(new Phrase("Diseases", font2));
//            cell3.setBorder(Rectangle.BOX);
//            table.addCell(cell3);
//
//            PdfPCell cell4 = new PdfPCell(new Phrase("Doctor ", font2));
//            cell4.setBorder(Rectangle.BOX);
//            table.addCell(cell4);
//
//            PdfPCell cell5 = new PdfPCell(new Phrase("Date", font2));
//            cell5.setBorder(Rectangle.BOX);
//            table.addCell(cell5);
//
//
//          
//            PdfPCell pdfc1;
//            PdfPCell pdfc2;
//            PdfPCell pdfc3;
//            PdfPCell pdfc4;
//            PdfPCell pdfc5;
//            int i = 1;
//
//            for (Consultation cons : consLists = new ConsultationDao().view("from Consultation c WHERE c.doctors.hospital.Id='" + loadName2() + "'")) {
//                pdfc1 = new PdfPCell(new Phrase(cons.getId().toString(), font6));
//                pdfc1.setBorder(Rectangle.BOX);
//                table.addCell(pdfc1);
//
//                pdfc2 = new PdfPCell(new Phrase(cons.getPatients_Name(), font6));
//                pdfc2.setBorder(Rectangle.BOX);
//                table.addCell(pdfc2);
//                
//                pdfc3 = new PdfPCell(new Phrase(cons.getDiseases().getName(), font6));
//                pdfc3.setBorder(Rectangle.BOX);
//                table.addCell(pdfc3);
//
//                pdfc4 = new PdfPCell(new Phrase(cons.getDoctors().getFName()+" "+cons.getDoctors().getLName(), font6));
//                pdfc4.setBorder(Rectangle.BOX);
//                table.addCell(pdfc4);
//
//                pdfc5 = new PdfPCell(new Phrase(cons.getDate().toString(), font6));
//                pdfc5.setBorder(Rectangle.BOX);
//                table.addCell(pdfc5);
//
//
//            }
//            document.add(table);
//            Paragraph p = new Paragraph("\n\nPrinted On: " + new Date(), font1);
//            p.setAlignment(Element.ALIGN_RIGHT);
//            document.add(p);
//            Paragraph ps = new Paragraph("\n Non-Communicable Diseases System ", font1);
//            ps.setAlignment(Element.ALIGN_RIGHT);
//            document.add(ps);
//            document.close();
//            String fileName = "Consultations Report";
//
//            writePDFToResponse(context.getExternalContext(), baos, fileName);
//
//            context.responseComplete();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    private void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) {
        try {
            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader("Expires", "0");
            externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            externalContext.setResponseHeader("Pragma", "public");
            externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
            externalContext.setResponseContentLength(baos.size());
            OutputStream out = externalContext.getResponseOutputStream();
            baos.writeTo(out);
            externalContext.responseFlushBuffer();
        } catch (IOException e) {

        }
    }

    public Hospital getLoadHospitals() {
        return loadHospitals;
    }

    public void setLoadHospitals(Hospital loadHospitals) {
        this.loadHospitals = loadHospitals;
    }

    public List<Doctors> getDocList() {
        return docList;
    }

    public void setDocList(List<Doctors> docList) {
        this.docList = docList;
    }

    public Doctors getDoctors() {
        return doctors;
    }

    public void setDoctors(Doctors doctors) {
        this.doctors = doctors;
    }

    public User getU() {
        return u;
    }

    public void setU(User u) {
        this.u = u;
    }

    public Doctors getDocdetails() {
        return docdetails;
    }

    public void setDocdetails(Doctors docdetails) {
        this.docdetails = docdetails;
    }

    public DoctorsDao getDocDao() {
        return docDao;
    }

    public void setDocDao(DoctorsDao docDao) {
        this.docDao = docDao;
    }

    public List<Appointment> getAppList2() {
        return appList2;
    }

    public void setAppList2(List<Appointment> appList2) {
        this.appList2 = appList2;
    }

    public List<Appointment> getAppList3() {
        return appList3;
    }

    public void setAppList3(List<Appointment> appList3) {
        this.appList3 = appList3;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public String getNewUserName() {
        return NewUserName;
    }

    public void setNewUserName(String NewUserName) {
        this.NewUserName = NewUserName;
    }

    public String getOldPass() {
        return OldPass;
    }

    public void setOldPass(String OldPass) {
        this.OldPass = OldPass;
    }

    public String getNewPass() {
        return NewPass;
    }

    public void setNewPass(String NewPass) {
        this.NewPass = NewPass;
    }

    public String getConfNewPass() {
        return ConfNewPass;
    }

    public void setConfNewPass(String ConfNewPass) {
        this.ConfNewPass = ConfNewPass;
    }

    public List<Operation> getOpList2() {
        return opList2;
    }

    public void setOpList2(List<Operation> opList2) {
        this.opList2 = opList2;
    }

}
