import React, { useEffect, useState } from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import { useRegisterMutation } from "../store/api/serverApi";
import { useNavigate } from "react-router-dom";
import {
  Container,
  CssBaseline,
  Box,
  Typography,
  Button,
  Grid,
  ThemeProvider
} from "@mui/material";
import { Link } from "react-router-dom";
import AuthFormField from "../components/UI/FormField/AuthFormFiled";
import customTheme from "../models/customAuthTheme";

export interface RegisterArgs {
  username: string;
  password: string;
  name?: string;
  surname?: string;
  sex?: "man" | "woman";
  age?: number;
}

const RegistrationPage = () => {
  const navigate = useNavigate();
  const [post, { data, isLoading, isError, error }] = useRegisterMutation();
  const { register, formState, handleSubmit } = useForm<RegisterArgs>(); 

  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [name, setName] = useState<string>("");


  const onSubmit: SubmitHandler<RegisterArgs> = () => {
    const registerArgs: RegisterArgs = {
      username,
      password,
      name,
    };

    post(registerArgs);
  };

  useEffect(() => {
    if (data) {
      navigate("/login", { replace: true });
    }
  }, [data, navigate]);

  return (
    <ThemeProvider theme={customTheme}>
      <Container
        sx={{ backgroundColor: "#030014" }}
        component="main"
        maxWidth="xs"
      >
        <CssBaseline />
        <Box
          sx={{
            marginTop: 8,
            display: "flex",
            flexDirection: "column",
            alignItems: "center"
          }}
        >
          <Typography component="h1" variant="h4" sx={{ mt: 3, mb: 1, color: "white" }}>
            Sign up
          </Typography>
          <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate sx={{ mt: 1 }}>
            <AuthFormField
              label="username"
              onChange={(e) => setUsername(e.target.value)}

            />

            <AuthFormField
              label="password"
              onChange={(e) => setPassword(e.target.value)}

            />

            <AuthFormField
              label="name"
              onChange={(e) => setName(e.target.value)}
              />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2, backgroundColor: "#6002ee" }}
            >
              Sign In
            </Button>
            <Grid container>
              <Grid item sx={{ mt: 4, mb: 4 }}>
                <Link to="/login">
                  {"Уже есть аккаунт? Sing in"}
                </Link>
              </Grid>
            </Grid>
          </Box>
        </Box>
      </Container>
    </ThemeProvider>
  );
};

export default RegistrationPage;
